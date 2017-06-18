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
import com.eagle.contract.model.EagleEngineJobs;
import com.eagle.contract.model.EagleEngineStopLimitRequest;
import com.eagle.contract.model.EaglePositionEngineEmailData;
import com.eagle.contract.model.EaglePositionEngineResult;
import com.eagle.contract.model.EmailRequest;
import com.eagle.contract.model.Instrument;
import com.eagle.contract.model.InstrumentPosition;
import com.eagle.contract.model.InstrumentPositionState;
import com.eagle.contract.model.InstrumentPredictionData;
import com.eagle.contract.model.InstrumentRealTimeData;
import com.eagle.workflow.engine.config.EagleModelProperties;
import com.eagle.workflow.engine.config.EagleWorkFlowEngineProperties;
import com.eagle.workflow.engine.repository.CancelOrderDataJobRepository;
import com.eagle.workflow.engine.repository.EagleEngineEmailRepository;
import com.eagle.workflow.engine.repository.InstrumentRepository;
import com.eagle.workflow.engine.repository.JobStatus;
import com.eagle.workflow.engine.repository.PlaceOrderDataJobRepository;
import com.eagle.workflow.engine.repository.PositionDataJobRepository;
import com.eagle.workflow.engine.repository.RealTimeDataJobRepository;
import com.eagle.workflow.engine.service.EmailService;
import com.eagle.workflow.engine.service.StopLimitService;
import com.eagle.workflow.engine.store.EagleEngineDataProcessor;
import com.eagle.workflow.engine.tws.client.EagleTWSClient;
import com.eagle.workflow.engine.tws.data.providers.EaglePositionDataProvider;
import com.eagle.workflow.engine.tws.data.providers.EagleRealTimeMarketDataProvider;
import com.eagle.workflow.engine.utils.EagleEngineDateUtils;
import com.eagle.workflow.engine.utils.EagleEngineFileUtils;
import com.eagle.workflow.engine.utils.EagleEnginePriceCalculator;

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
	private CancelOrderDataJobRepository cancelOrderDataJobRepository;
	
	@Autowired
	private PlaceOrderDataJobRepository placeOrderDataJobRepository;
	
	@Autowired
	private RealTimeDataJobRepository realTimeDataJobRepository;
	
	@Autowired
	private EagleRealTimeMarketDataProvider eagleRealTimeMarketDataProvider;
	
	@Autowired
	private EagleTWSClient eagleTWSClient;
	
	@Autowired
	private EagleEnginePriceCalculator eagleEnginePriceCalculator;
	
	@Autowired
	private StopLimitService stopLimitService;
	
	@Autowired
	private EagleEngineDateUtils eagleEngineDateUtils;
	
	@Autowired
	private EagleEngineEmailRepository eagleEngineEmailRepository;
	
	@Autowired
	private EmailService emailService;
	
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
			
			// Email 
			EmailRequest emailRequest = new EmailRequest();
			emailRequest.setEmailSubject("Eagle - Position Details - "+eagleEngineDateUtils.getExtractDataRunDate());
			emailRequest.setEmailContent("");
			eagleEngineEmailRepository.addJobEmailRequest(EagleEngineJobs.POSTIONENGINE.name(), emailRequest);
			EaglePositionEngineEmailData emailData = new EaglePositionEngineEmailData();
			// Step 1: Cancel all the positions from the profile.
			boolean cancelOrdersStatus = cancelOpenPositions();
			LOGGER.info("cancel open Orders Status : "+cancelOrdersStatus);
			if(!cancelOrdersStatus){
				throw new EagleException(EagleError.FAILED_TO_CANCEL_ALL_OPEN_ORDERS);
			}
			for (Instrument instrument : instrumentsList) {
				LOGGER.info("Position Engine Instrument ["+instrument.getSymbol()+"] - Started");
				predictionFilePath = predictionsDirectory + instrument.getSymbol() + PREDICTION_FILE_SUFFIX;
				Path path = Paths.get(predictionFilePath);
				if (path == null || Files.notExists(path)) {
					throw new EagleException(EagleError.INVALID_PREDICTION_PATH, predictionFilePath);
				}
				emailData.setSymbol(instrument.getSymbol());
				// Step 2a: fetch the last record from the prediction data
				predictionData = (InstrumentPredictionData) dataProcessor
						.getLastRecord(InstrumentPredictionData.class, predictionFilePath, true);
				if (predictionData == null) {
					throw new EagleException(EagleError.NO_PREDICTION_DATA, predictionFilePath);
				}
				LOGGER.info("Instrument ["+instrument.getSymbol()+"] - prediction Data: "+predictionData.toString());

				emailData.setPredictionData(predictionData);
				
				// Step 2b: desiredPosition
				InstrumentPositionState nextDayPosition  = desiredPosition(predictionData.getNextdretPredicted(), instrument.getPredictionValue());
				emailData.setNextDayPosition(nextDayPosition);
				
				// Step 3: Query Interactive Broker to get Positions in the Portfolio.
				int openPosition = getInstrumentOpenPosition(instrument, twsAccounts);
				emailData.setIBPositions(openPosition);
				LOGGER.info("Position from Interactive Broker ["+instrument.getSymbol()+"]: "+openPosition);

				// Step 4: Read the leverage factor
				int leverageFactor = instrument.getLeverageFactor();
				emailData.setLeverageFactor(leverageFactor);
				LOGGER.info("Leverage Factor from Configuraiton ["+instrument.getSymbol()+"]: "+leverageFactor);

				InstrumentPositionState todayPosition = determinePosition(openPosition);
				emailData.setTodayPosition(todayPosition);
				
				LOGGER.info("Is Position Long Or Short ["+instrument.getSymbol()+"]: "+todayPosition.name());

				// Step 5: Position Manager
				EaglePositionEngineResult result = applyPositionRules(todayPosition, nextDayPosition, leverageFactor, openPosition);
				LOGGER.info("Appled Position rules ["+instrument.getSymbol()+"]: "+result.toString());

				if(result.getPosition() != InstrumentPositionState.DO_NOTHING){

					// Step 6: Get current price for instrument from Interactive Broker.
					double currentPrice = getInstrumentCurrentPrice(instrument, result.getPosition());

					// Step 7: Calculating the limit price of the order
					/* Here is the sample for limit price calculations
					 * assume current price at this sec.. $2001
					 *   -  If order is sell (from EaglePositionEngineResult), (2001 -  instrument.getPriceLimit())
					 *   -  If order is Buy (from EaglePositionEngineResult), (2001 +  instrument.getPriceLimit())
					 */

					LOGGER.info("Instrument ["+instrument.getSymbol()+"] current price from IB: "+currentPrice);
					double limitPrice = currentPrice;
					double stopLimitPrice = currentPrice;
					double stopPrice = currentPrice;
					
					// Calculate Limit Price
					limitPrice = eagleEnginePriceCalculator.getLimitPrice(instrument, result.getPosition(), currentPrice);
					// Calculate Stop Price
					stopPrice  = eagleEnginePriceCalculator.getStopPrice(instrument, result.getPosition(), currentPrice);
					// Calculate Stop Limit Price
					stopLimitPrice  = eagleEnginePriceCalculator.getStopLimitPrice(instrument, result.getPosition(), currentPrice);

					result.setLimitPrice(limitPrice);
					result.setStopLimitPrice(stopLimitPrice);
					result.setStopPrice(stopPrice);
					LOGGER.info("Instrument ["+instrument.getSymbol()+"], Postion ["+result.getPosition().name()+"], limit price: "+limitPrice);

					// step 8 : submit order for tomorrows position (IB)
					submitOrder(instrument, result, twsAccounts.get(0));
					emailData.setOrderSubmited(true);
					// Step 8: Wait for order execution status (from IB)
					//FIXME:

					//int finalOpenPosition = getInstrumentOpenPosition(instrument, twsAccounts);
				} else {
					emailData.setOrderSubmited(false);
					LOGGER.info("Instrument ["+instrument.getSymbol()+"] Position state is DO_NOTHING");
				}
				emailData.setEaglePositionEngineResult(result);
				updateEmailContent(emailData.getEmailContent());
			}
			emailRequest = eagleEngineEmailRepository.getJobEmailRequest(EagleEngineJobs.POSTIONENGINE.name());
			emailService.send(emailRequest);
			eagleEngineEmailRepository.removeJobEmailRequests(EagleEngineJobs.POSTIONENGINE.name());
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
	private InstrumentPositionState desiredPosition(double nextDayPredection, int predictionValue){
		if(nextDayPredection*predictionValue == 1){
			return InstrumentPositionState.LONG;
		}
		return InstrumentPositionState.SHORT;
	}
	private InstrumentPositionState determinePosition(int position){
		if(position < 0){
			return InstrumentPositionState.SHORT;
		} else {
			return InstrumentPositionState.LONG;
		}
	}
	
	private EaglePositionEngineResult applyPositionRules(InstrumentPositionState todayPosition,
			InstrumentPositionState nextDayPredection, int leverageFactor, int openPosition) {
		EaglePositionEngineResult eaglePositionEngineResult = new EaglePositionEngineResult();
		if (InstrumentPositionState.LONG == todayPosition && InstrumentPositionState.LONG == nextDayPredection) {
			// nothing to do
			eaglePositionEngineResult.setPosition(InstrumentPositionState.DO_NOTHING);
		} else if (InstrumentPositionState.LONG == todayPosition && InstrumentPositionState.SHORT == nextDayPredection) {
			// Clean all open Orders that will wipe out all STOP-LOSS orders
			// Need to submit a limit order to sell shares
			eaglePositionEngineResult.setPosition(InstrumentPositionState.SELL);
			// Find the contract count using Formula for find the contact
			// Ex: if leverageFactor = 2 and today Open Positions is 2 then tomorrowPrediction(sell) = -2-2 = abs(-4) = 4  
			eaglePositionEngineResult.setContractCount(Math.abs(-leverageFactor - openPosition));
		} else if(InstrumentPositionState.SHORT == todayPosition && InstrumentPositionState.LONG == nextDayPredection){
			// Need to submit a limit order to buy
			eaglePositionEngineResult.setPosition(InstrumentPositionState.BUY);
			// Find the contract count using Formula for find the contact
			// Ex: if leverageFactor = 2 and today Open Positions is 2 then tomorrowPrediction(buy) = 2+2 =  4  
			eaglePositionEngineResult.setContractCount(Math.abs(leverageFactor + openPosition));
		} else if (InstrumentPositionState.SHORT == todayPosition && InstrumentPositionState.SHORT == nextDayPredection) {
			// nothing to do
			eaglePositionEngineResult.setPosition(InstrumentPositionState.DO_NOTHING);
		}
		return eaglePositionEngineResult;
	}
	
	private double getInstrumentCurrentPrice(Instrument instrument, InstrumentPositionState position){
		try {
			//register the job in job Repository.
			realTimeDataJobRepository.addJob(instrument.getSymbol(), JobStatus.INPROGRESS);
			//make API Call to IB
			eagleTWSClient.getCurrentInstrumentPrice(instrument);
			ListenableFuture<Boolean> jobStatusListen = realTimeDataJobRepository.isJobsDone(instrument.getSymbol());
			if (jobStatusListen.get()) {
				InstrumentRealTimeData realTimeData = eagleRealTimeMarketDataProvider.getInstrumentRealTimeData(instrument.getSymbol());
				if(position == InstrumentPositionState.BUY){
					return realTimeData.getBid().doubleValue();
				}
				return realTimeData.getAsk().doubleValue();
			}
			return 0.0;
		} catch (EagleException e) {
			throw e;
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_GET_POSITION, instrument.getSymbol(), e.getMessage());
		}
	}
	
	private int getInstrumentOpenPosition(Instrument instrument, List<String> twsAccounts){
		try {
			eagleTWSClient.getInstrumentOpenPosition(instrument);
			positionDataJobRepository.addJob(instrument.getSymbol(), JobStatus.INPROGRESS);
			//eagleTWSClient.getPortifolioPosition(instrument,twsAccounts.get(0));
			//positionDataJobRepository.addJob(instrument.getSymbol(), JobStatus.INPROGRESS);
			ListenableFuture<Boolean> jobStatusListen = positionDataJobRepository.isJobsDone(instrument.getSymbol());
			if(jobStatusListen.get()){
				InstrumentPosition instrumentPosition = EaglePositionDataProvider.getInstrumentPositionData(instrument.getSymbol());
				return (int)instrumentPosition.getPosition();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new EagleException(EagleError.FAILED_TO_GET_POSITION, instrument.getSymbol(), e.getMessage());
		} catch (EagleException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EagleException(EagleError.FAILED_TO_GET_POSITION, instrument.getSymbol(), e.getMessage());
		}
		return 0;
	}
	
	private boolean cancelOpenPositions(){
		try {
			eagleTWSClient.cancelAllOpenOrders();
			/*cancelOrderDataJobRepository.addJob("CANCEL_ALL_ORDERS", JobStatus.INPROGRESS);
			ListenableFuture<Boolean> jobStatusListen = cancelOrderDataJobRepository.isJobsDone("CANCEL_ALL_ORDERS");
			if(jobStatusListen.get()){
				return true;
			}*/
			return true;
		} catch (EagleException e) {
			throw e;
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_CANCEL_ALL_OPEN_ORDERS, e.getMessage(),e );
		}
	}
	
	private void submitOrder(Instrument instrument, EaglePositionEngineResult positionEngineResult, String account){
		try {
			eagleTWSClient.placeOrder(instrument, positionEngineResult, account);
			placeOrderDataJobRepository.addJob(instrument.getSymbol(), JobStatus.INPROGRESS);
			EagleEngineStopLimitRequest request = new EagleEngineStopLimitRequest();
			request.setInstrument(instrument);
			request.setPositionResult(positionEngineResult);
			request.setAccountName(account);
			placeOrderDataJobRepository.addJobDetails(instrument.getSymbol(), request);
		} catch (EagleException e) {
			throw e;
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_SUBMIT_ORDER, instrument.getSymbol(), e.getMessage());
		}
	}
	
	private void setupStopLimit(Instrument instrument, EaglePositionEngineResult positionEngineResult, String account){
		try {
			eagleTWSClient.placeOrder(instrument, positionEngineResult, account);
			placeOrderDataJobRepository.addJob(instrument.getSymbol(), JobStatus.INPROGRESS);
		} catch (EagleException e) {
			throw e;
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_SUBMIT_ORDER, instrument.getSymbol(), e.getMessage());
		}
	}
	
	private void updateEmailContent(String emailData){
		EmailRequest emailRequest = eagleEngineEmailRepository.getJobEmailRequest(EagleEngineJobs.POSTIONENGINE.name());
		StringBuilder emailContent = new StringBuilder(emailRequest.getEmailContent());
		emailContent.append(emailData);
		emailContent.append("<br> <br>");
		emailRequest.setEmailContent(emailContent.toString());
		eagleEngineEmailRepository.addJobEmailRequest(EagleEngineJobs.POSTIONENGINE.name(), emailRequest);
	}
}
