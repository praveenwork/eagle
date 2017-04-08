package com.eagle.workflow.engine.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.model.Instrument;
import com.eagle.workflow.engine.repository.ExtractDataJobRepository;
import com.eagle.workflow.engine.repository.InstrumentRepository;
import com.eagle.workflow.engine.repository.JobStatus;
import com.eagle.workflow.engine.tws.client.EagleTWSClient;
import org.springframework.util.concurrent.ListenableFuture;

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
	
	
	/* (non-Javadoc)
	 * @see com.eagle.workflow.engine.service.DataExtractorService#extractData()
	 */
	@Override
	public boolean extractData() throws EagleException {
		try {
			LOGGER.debug("Extracting service in progress ...");
			List<Instrument> instrumentsList = instrumentRepository.getInstruments();
			for (Instrument instrument : instrumentsList) {
				eagleTWSClient.extractHistoricalData(instrument);
				extractDataJobRepository.addJob(instrument.getSymbol(), JobStatus.INPROGRESS);
			}
			ListenableFuture<Boolean> jobStatusListen = extractDataJobRepository.isJobsDone();
			LOGGER.debug("jobStatusListen is done?"+jobStatusListen.isDone());
//			while (!jobStatusListen.isDone()) {
//				LOGGER.debug("jobStatusListen is not done.. sleeping 2 sec");
//				Thread.sleep(2);
//			}
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
