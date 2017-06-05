package com.eagle.workflow.engine.schedulers;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InstrumentSheduler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentSheduler.class);
	
	 @Autowired
	  private JobLauncher jobLauncher;

	  @Autowired
	  private Job job;
	
	//@Scheduled(cron="0 0 16 ? * MON-FRI")
	@Scheduled(cron="0 0/59 * 1/1 * ?")
	@Async(value = "instrumentScheduler")
	public void runScheduleJob(){
		try {
			LOGGER.debug("Instrument Scheduler process started...");
			JobParameters param =
					new JobParametersBuilder().addString("date", new Date().toString()).toJobParameters();
			jobLauncher.run(job, param);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			LOGGER.error("Schedule process Failed..",e.getMessage(),e);
			e.printStackTrace();
		}
	}
}

