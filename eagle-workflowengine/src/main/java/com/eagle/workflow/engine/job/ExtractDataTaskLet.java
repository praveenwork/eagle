package com.eagle.workflow.engine.job;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.model.EagleEngineJobs;
import com.eagle.contract.model.EmailRequest;
import com.eagle.workflow.engine.repository.EagleEngineEmailRepository;
import com.eagle.workflow.engine.service.DataExtractorService;
import com.eagle.workflow.engine.service.EmailService;
import com.eagle.workflow.engine.utils.EagleEngineDateUtils;

/**
 * @author ppasupuleti
 *
 */
@Component
public class ExtractDataTaskLet implements Tasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtractDataTaskLet.class);


	@Autowired
	private DataExtractorService dataExtractorService;
	
	@Autowired
	private EagleEngineEmailRepository eagleEngineEmailRepository;
	
	@Autowired
	private EagleEngineDateUtils eagleEngineDateUtils;

	@Autowired
	private EmailService emailService;
	
	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
		String jobName =  jobExecution.getJobInstance().getJobName();
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setEmailSubject("Eagle - Extract Data - "+eagleEngineDateUtils.getExtractDataRunDate());
		emailRequest.setEmailContent("");
		eagleEngineEmailRepository.addJobEmailRequest(EagleEngineJobs.EXTRACTDATA.name(), emailRequest);
		LOGGER.debug("ExtractData Tasklet step in progress..");
		try {
			dataExtractorService.extractData(); 
			emailRequest = eagleEngineEmailRepository.getJobEmailRequest(EagleEngineJobs.EXTRACTDATA.name());
			emailService.send(emailRequest);
			eagleEngineEmailRepository.removeJobEmailRequests(EagleEngineJobs.EXTRACTDATA.name());
		} catch (EagleException e) {
			throw e;
		}  catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_EXTRACT_DATA, e.getMessage(),e);
		}
		LOGGER.debug("ExtractData Tasklet step completed");
		return RepeatStatus.FINISHED;
	}

}
