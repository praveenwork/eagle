package com.eagle.workflow.engine.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.constants.EagleContractConstants;
import com.eagle.contract.constants.InstrumentHistoricalDataProvider;
import com.eagle.contract.model.Instrument;
import com.eagle.contract.model.InstrumentHistoricalData;
import com.eagle.workflow.engine.repository.ExtractDataJobRepository;
import com.eagle.workflow.engine.repository.InstrumentRepository;
import com.eagle.workflow.engine.repository.JobStatus;
import com.eagle.workflow.engine.store.EagleEngineDataProcessor;
import com.eagle.workflow.engine.tws.client.EagleTWSClient;
import com.eagle.workflow.engine.utils.EagleEngineDateUtils;
import com.eagle.workflow.engine.utils.EagleEngineFileUtils;
import com.eagle.workflow.engine.yahoofinance.client.YahooFinanceClient;

/**
 * @author ppasupuleti
 *
 */
@Service
public class DataExtractorServiceImpl implements DataExtractorService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExtractorServiceImpl.class);
	
	@Autowired
	private EagleTWSClient eagleTWSClient;
	
	@Autowired
	private YahooFinanceClient yahooFinanceClient;
	
	@Autowired
	private InstrumentRepository instrumentRepository;
	
	@Autowired
	private ExtractDataJobRepository extractDataJobRepository;
	
	@Autowired
	private EagleEngineFileUtils eagleEngineFileUtils;
	
	@Autowired
	private EagleEngineDateUtils eagleEngineDateUtils;
	
	@Autowired
	private EagleEngineDataProcessor<InstrumentHistoricalData> dataProcessor;
	
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
	/* (non-Javadoc)
	 * @see com.eagle.workflow.engine.service.DataExtractorService#extractData()
	 */
	@Override
	public boolean extractData() throws EagleException {
		try {
			LOGGER.debug("Extracting service in progress ...");
			List<Instrument> instrumentsList = instrumentRepository.getInstruments();
			String rawDataDirectory = eagleEngineFileUtils.getRawDataPath();
			ClassPathResource classpathResource = new ClassPathResource(rawDataDirectory);
			for (Instrument instrument : instrumentsList) {
				int duration = 9;
				String instrumentStorePath = classpathResource.getPath() + instrument.getSymbol() + ".csv";
				if(Files.exists(Paths.get(instrumentStorePath))){
					//Fetch the last historical Record from the instrument store file.
					InstrumentHistoricalData latestHistoricalData = dataProcessor.getLastRecord(InstrumentHistoricalData.class, instrumentStorePath);
					if (latestHistoricalData != null) {
						duration = eagleEngineDateUtils.getHistoricalDataDuration(LocalDate.parse(latestHistoricalData.getDate(), dateFormatter),instrument.getHistoricalDataProvider());
					} else {
						LOGGER.info("No historical data found for instrument : [" + instrument.getSymbol()
								+ "] , hence setting dufault duration : " + EagleContractConstants.DEFAULT_DURATION);
						duration = EagleContractConstants.DEFAULT_DURATION;
					}
				} else{
					duration = EagleContractConstants.DEFAULT_DURATION;
				}
				
				if (duration <= 0) {
					LOGGER.info("Today's data already extracted for instrument:"+instrument.getSymbol());
				} else {
					if (instrument.getHistoricalDataProvider()
							.equalsIgnoreCase(InstrumentHistoricalDataProvider.IB.name())) {
						eagleTWSClient.extractHistoricalData(instrument, duration); 
						extractDataJobRepository.addJob(instrument.getSymbol(), JobStatus.INPROGRESS);
					} else {
						yahooFinanceClient.extractHistoricalData(instrument, duration);
					}
				}
			}
			
			ListenableFuture<Boolean> jobStatusListen = extractDataJobRepository.isJobsDone();
			LOGGER.debug("jobStatusListen is done?"+jobStatusListen.isDone());
			if(jobStatusListen.get()){
				LOGGER.debug("jobStatusListen.get() true");
				return true;
			}else{
				LOGGER.debug("jobStatusListen.get() false");
			}

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
