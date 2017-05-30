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
		LOGGER.debug("*** Position Engine Tasklet in progress...***");
		try {
			String predictionsDirectory =  eagleEngineFileUtils.getModelOutputPath();
			
			List<Instrument> instrumentsList = instrumentRepository.getInstruments();
			String predictionFilePath = null;
			InstrumentPredictionData predictionData = null;
			List<String> twsAccounts = eagleTWSClient.getAccounts();
			for (Instrument instrument : instrumentsList) {
				predictionFilePath = predictionsDirectory + instrument.getSymbol() + PREDICTION_FILE_SUFFIX;
				Path path = Paths.get(predictionFilePath);
				if (path == null || Files.notExists(path)) {
					throw new EagleException(EagleError.INVALID_PREDICTION_PATH, predictionFilePath);
				}
				// Step 1a: fetch the last record from the prediction data
				predictionData = (InstrumentPredictionData) dataProcessor
						.getLastRecord(InstrumentPredictionData.class, predictionFilePath, true);
				if (predictionData == null) {
					throw new EagleException(EagleError.NO_PREDICTION_DATA, predictionFilePath);
				}
				LOGGER.info(predictionData.toString());

				// Step 1b: desiredPosition
				InstrumentPosition nextDayPosition  = desiredPosition(predictionData.getNextdretPredicted(), instrument.getPredictionValue());

				// Step 2: Query Interactive Broker to get Positions in the Portfolio.
				int openPosition = getInstrumentOpenPosition(instrument, twsAccounts);
				LOGGER.info("Open Position from Interactive Broker: "+openPosition);

				// Step 3: Read the leverage factor
				int leverageFactor = instrument.getLeverageFactor();
				LOGGER.info("Leverage Factor from Configuraiton: "+leverageFactor);

				InstrumentPosition todayPosition = determinePosition(openPosition);
				LOGGER.info("Is Position Long Or Short: "+todayPosition.name());

				// Step 4: Position Manager
				EaglePositionEngineResult result = applyPositionRules(todayPosition, nextDayPosition, leverageFactor, openPosition);
				LOGGER.info("Appled Position rules: "+result.toString());

				// Step 5: Calculating the limit price of the order

				// FIXME: Make api call to get current price of ES from Interactive Broker.
				/*ex: current price at this sec.. $2001
				 *    If order is sell (from EaglePositionEngineResult), (2001 -  instrument.getPriceLimit())
				 *    If order is Buy (from EaglePositionEngineResult), (2001 +  instrument.getPriceLimit())
				 */    

				// Step 6: clean all open orders calls (IB)

				// step 7 : submit order for tomorrows position (IB)

				// Step 8: Wait for order execution status (from IB)

				// Step 9: Create STOP LIMIT Order (IB)


				int finalOpenPosition = getInstrumentOpenPosition(instrument, twsAccounts);
			}
		} catch (EagleException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EagleException(EagleError.FAILED_TO_EXECUTE_ENRICHDATA_STEP, e, e.getMessage());
		}
		LOGGER.debug("*** Position Engine Tasklet completed ***");
		return RepeatStatus.FINISHED;
	}
	
	//-----------Helpers--------------
	private InstrumentPosition desiredPosition(double nextDayPredection, int predictionValue){
		if(nextDayPredection*predictionValue == 1){
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
	
	private EaglePositionEngineResult applyPositionRules(InstrumentPosition todayPosition,
			InstrumentPosition nextDayPredection, int leverageFactor, int openPosition) {
		EaglePositionEngineResult eaglePositionEngineResult = new EaglePositionEngineResult();
		if (InstrumentPosition.LONG == todayPosition && InstrumentPosition.LONG == nextDayPredection) {
			// nothing to do
			eaglePositionEngineResult.setPosition(InstrumentPosition.DO_NOTHING);
		} else if (InstrumentPosition.LONG == todayPosition && InstrumentPosition.SHORT == nextDayPredection) {
			// Need to submit a limit order to sell shares
			eaglePositionEngineResult.setPosition(InstrumentPosition.SELL);
			// Find the contract count using Formula for find the contact
			// Ex: if leverageFactor = 2 and today Open Positions is 2 then tomorrowPrediction(sell) = -2-2 = abs(-4) = 4  
			eaglePositionEngineResult.setContractCount(Math.abs(-leverageFactor - openPosition));
		} else if(InstrumentPosition.SHORT == todayPosition && InstrumentPosition.LONG == nextDayPredection){
			// Need to submit a limit order to buy
			eaglePositionEngineResult.setPosition(InstrumentPosition.BUY);
			// Find the contract count using Formula for find the contact
			// Ex: if leverageFactor = 2 and today Open Positions is 2 then tomorrowPrediction(buy) = 2+2 =  4  
			eaglePositionEngineResult.setContractCount(Math.abs(leverageFactor + openPosition));
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
