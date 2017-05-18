package com.eagle.workflow.engine.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.model.Instrument;
import com.eagle.contract.model.InstrumentHistoricalData;
import com.eagle.workflow.engine.repository.ExtractDataJobRepository;
import com.eagle.workflow.engine.repository.InstrumentRepository;
import com.eagle.workflow.engine.repository.JobStatus;
import com.eagle.workflow.engine.store.EagleEngineDataProcessor;
import com.eagle.workflow.engine.tws.client.EagleTWSClient;
import com.eagle.workflow.engine.utils.EagleEngineFileUtils;

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
	private InstrumentRepository instrumentRepository;
	
	@Autowired
	private ExtractDataJobRepository extractDataJobRepository;
	
	@Autowired
	private EagleEngineFileUtils eagleEngineFileUtils;
	
	@Autowired
	private EagleEngineDataProcessor<InstrumentHistoricalData> dataProcessor;
	
	private static final int MAX_IB_SUPPORTED_HISTORICAL_DAYS = 365;
	private static final int DEFAULT_DURATION = 9;
	
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
				//Fetch the last historical Record from the instrument store file.
				InstrumentHistoricalData latestHistoricalData = dataProcessor.getLastRecord(InstrumentHistoricalData.class, instrumentStorePath);
				if (latestHistoricalData != null) {
					duration = historicalDataDuration(LocalDate.parse(latestHistoricalData.getDate(), dateFormatter));
				} else {
					LOGGER.info("No historical data found for instrument : ["+instrument.getSymbol()+"] , hence setting dufault duration : "+DEFAULT_DURATION);
					duration = DEFAULT_DURATION;
				}
				if (duration <= 0) {
					LOGGER.info("Today's data already extracted for instrument:"+instrument.getSymbol());
				} else {
					eagleTWSClient.extractHistoricalData(instrument, duration); 
					extractDataJobRepository.addJob(instrument.getSymbol(), JobStatus.INPROGRESS);
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
	
	//--------------Helpers----------------
	private int historicalDataDuration(LocalDate lastRecordDate) {
		int duration = 0;
		LocalDate today = LocalDate.now();
		int addDays = 1; // Need to fetch data from LastRecordDay after
		int weeks = (int) ChronoUnit.WEEKS.between(lastRecordDate.plusDays(addDays), today);
		int days = (int) ChronoUnit.DAYS.between(lastRecordDate.plusDays(addDays), today);
		duration = days - (weeks * 2);
		// if the lastRecordDate is a Friday and
		if ((days % weeks != 0) && (lastRecordDate.getDayOfWeek() == DayOfWeek.FRIDAY)) {
			duration = duration - 2;
		}
		if (duration > MAX_IB_SUPPORTED_HISTORICAL_DAYS) {
			duration = MAX_IB_SUPPORTED_HISTORICAL_DAYS;
		}
		return duration;
	}
	
}
