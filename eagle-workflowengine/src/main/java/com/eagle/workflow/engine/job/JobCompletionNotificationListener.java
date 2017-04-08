package com.eagle.workflow.engine.job;


import static java.lang.String.format;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;

import com.eagle.contract.model.EmailRequest;

import com.eagle.workflow.engine.service.EmailService;

import org.springframework.stereotype.Component;

/**
 * @author ppasupuleti
 *
 */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
	private static final Logger LOGGER = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	@Autowired
	private EmailService emailService;
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName() ;
		LOGGER.debug(jobName + "Started..");
	}


	@Override
	public void afterJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName() ;
		String exitStatus = jobExecution.getExitStatus().getExitCode();
		List<Throwable> exceptionList = jobExecution.getAllFailureExceptions();
		String emailSubject = null;
		String emailContent = null;
		if (exceptionList.size() > 0) {
			emailSubject = format(
					"Job: %s, job execution id: %d, job id: %d, started on %s, "
							+ "contains %d error(s), job exit status: %s",
					jobName, jobExecution.getId(), jobExecution.getJobId(), jobExecution.getStartTime().toString(),
					exceptionList.size(), exitStatus);
			emailContent = emailSubject;//FIXME: provide the email content
		} else {
			emailSubject = format("Job: %s, job execution id: %d, job id: %d, succesfully completed status: %s",
					jobName, jobExecution.getId(), jobExecution.getJobId(), jobExecution.getStartTime().toString(),
					exceptionList.size(), exitStatus);
			emailContent = emailSubject; //FIXME: provide the email content
			LOGGER.debug("Email Subject: "+emailSubject);
			LOGGER.debug("Email Content: "+emailContent);
			
			//sending Mail
			LOGGER.debug("Sending email...");
			EmailRequest emailRequest = new EmailRequest();
			emailRequest.setEmailContent(emailContent);
			emailRequest.setEmailSubject(emailSubject);
			emailService.send(emailRequest);
			
		}
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			LOGGER.debug(jobName + "completed.");
			// send the email notification on success
		}


	}
}
