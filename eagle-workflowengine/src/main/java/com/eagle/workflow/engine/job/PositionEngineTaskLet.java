package com.eagle.workflow.engine.job;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.constants.InstrumentPosition;
import com.eagle.contract.model.AccountPosition;
import com.eagle.contract.model.Instrument;
import com.eagle.contract.model.InstrumentPredictionData;
import com.eagle.workflow.engine.config.EagleModelProperties;
import com.eagle.workflow.engine.config.EagleWorkFlowEngineProperties;
import com.eagle.workflow.engine.repository.InstrumentRepository;
import com.eagle.workflow.engine.repository.JobStatus;
import com.eagle.workflow.engine.repository.PositionDataJobRepository;
import com.eagle.workflow.engine.store.EagleEngineDataProcessor;
import com.eagle.workflow.engine.tws.client.EagleTWSClient;
import com.eagle.workflow.engine.tws.data.providers.EagleAccountDataProvider;
import com.eagle.workflow.engine.utils.EagleEngineFileUtils;
import com.eagle.workflow.engine.utils.EaglePositionEngineResult;

/**
 * @author ppasupuleti
 *
 */
@Component
public class PositionEngineTaskLet implements Tasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(PositionEngineTaskLet.class);

	private EagleModelProperties eagleModelProperties;
	
	private EagleWorkFlowEngineProperties engineProperties;
	
	@Autowired
	private InstrumentRepository instrumentRepository;
	
	@Autowired
	private EagleEngineFileUtils eagleEngineFileUtils;
	
	@Autowired
	private EagleEngineDataProcessor dataProcessor;
	
	@Autowired
	private PositionDataJobRepository positionDataJobRepository;
	
	
	@Autowired
	private EagleTWSClient eagleTWSClient;
	
	
	private static final String PREDICTION_FILE_SUFFIX = "_predictions.csv";
	
	public PositionEngineTaskLet(EagleModelProperties eagleModelProperties, EagleWorkFlowEngineProperties engineProperties) {
		this.eagleModelProperties = eagleModelProperties;
		this.engineProperties = engineProperties;
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		LOGGER.debug("Position Engine Tasklet in progress...");
		try {
			String predictionsDirectory =  eagleEngineFileUtils.getModelOutputPath();
			
			List<Instrument> instrumentsList = instrumentRepository.getInstruments();
			String predictionFilePath = null;
			InstrumentPredictionData predictionData = null;
			List<String> twsAccounts = eagleTWSClient.getAccounts();
			for (Instrument instrument : instrumentsList) {
				if ("es".equalsIgnoreCase(instrument.getSymbol())) { //FIXME: delete this condition
					predictionFilePath = predictionsDirectory + instrument.getSymbol() + PREDICTION_FILE_SUFFIX;
					Path path = Paths.get(predictionFilePath);
					if (path == null || Files.notExists(path)) {
						throw new EagleException(EagleError.INVALID_PATH, predictionFilePath);
					}
					// Step 1a: fetch the last record from the prediction data
					predictionData = (InstrumentPredictionData) dataProcessor
							.getLastRecord(InstrumentPredictionData.class, predictionFilePath, true);
					if (predictionData == null) {
						throw new EagleException(EagleError.NO_PREDICTION_DATA, predictionFilePath);
					}
					LOGGER.info(predictionData.toString());
					
					// Step 1b: determinePrediction
					InstrumentPosition nextDayPredictoin  = determinePrediction(predictionData.getNextdretPredicted());
					
					// Step 2: Query Interactive Broker to get Positions in the Portfolio.
					int openPosition = getInstrumentOpenPosition(instrument, twsAccounts);
					
					// Step 3: Read the leverage factor
					int leverageFactor = instrument.getLeverageFactor();
					
					InstrumentPosition position = determinePosition(openPosition);
					// Step 4: Position Manager
					EaglePositionEngineResult result = applyPositionRules(position, nextDayPredictoin,leverageFactor);
					
					// Step 5: check the position
					int finalOpenPosition = getInstrumentOpenPosition(instrument, twsAccounts);
				}
			}
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_EXECUTE_ENRICHDATA_STEP, e, e.getMessage());
		}
		LOGGER.debug("Position Engine Tasklet completed");
		return RepeatStatus.FINISHED;
	}
	
	//-----------Helpers--------------
	private InstrumentPosition determinePrediction(double nextDayPredection){
		if(nextDayPredection == 1){
			return InstrumentPosition.LONG;
		}
		return InstrumentPosition.SHORT;
	}
	private InstrumentPosition determinePosition(int position){
		if(position < 0){
			return InstrumentPosition.SHORT;
		} else {
			return InstrumentPosition.LONG;
		}
	}
	
	private EaglePositionEngineResult applyPositionRules(InstrumentPosition position,InstrumentPosition nextDayPredection, int leverageFactor){
		EaglePositionEngineResult eaglePositionEngineResult = new EaglePositionEngineResult();
		if (InstrumentPosition.LONG == position && InstrumentPosition.LONG == nextDayPredection) {
			// nothing to do
			eaglePositionEngineResult.setPosition(InstrumentPosition.DO_NOTHING);
		} else if (InstrumentPosition.LONG == position && InstrumentPosition.SHORT == nextDayPredection) {
			// Need to submit a limit order to sell shares
			eaglePositionEngineResult.setPosition(InstrumentPosition.ASK);
			//With a leverageFactor 1, we submit a limit order to sell 2 contracts of the instruments
		} else if(InstrumentPosition.SHORT == position && InstrumentPosition.LONG == nextDayPredection){
			// Need to submit a limit order to buy
			eaglePositionEngineResult.setPosition(InstrumentPosition.BID);
		}
		return eaglePositionEngineResult;
	}
	
	
	private int getInstrumentOpenPosition(Instrument instrument, List<String> twsAccounts){
		try {
			eagleTWSClient.getPortifolioPosition(instrument,twsAccounts.get(0));
			positionDataJobRepository.addJob(instrument.getSymbol(), JobStatus.INPROGRESS);
			ListenableFuture<Boolean> jobStatusListen = positionDataJobRepository.isJobsDone(instrument.getSymbol());
			if(jobStatusListen.get()){
				AccountPosition accountPosition = EagleAccountDataProvider.getAcccountPositionData(instrument.getSymbol());
				return accountPosition.getPosition();
			}
		} catch (InterruptedException | ExecutionException e) {
			throw new EagleException(EagleError.FAILED_TO_GET_POSITION, instrument.getSymbol(), e.getMessage());
		} catch (EagleException e) {
			throw e;
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_GET_POSITION, instrument.getSymbol(), e.getMessage());
		}
		return 0;
	}
}
