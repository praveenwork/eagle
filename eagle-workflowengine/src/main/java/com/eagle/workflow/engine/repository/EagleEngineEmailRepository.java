package com.eagle.workflow.engine.repository;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.eagle.contract.model.EmailRequest;

/**
 * @author ppasupuleti
 *
 */
@Repository
public class EagleEngineEmailRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(EagleEngineEmailRepository.class);
	
	private static ConcurrentHashMap<String, EmailRequest> jobEmailRequestMap = new ConcurrentHashMap<>();
	
	public void addJobEmailRequest(String jobName, EmailRequest emailRequest){
		jobEmailRequestMap.put(jobName, emailRequest);
	}
	
	public EmailRequest getJobEmailRequest(String jobName){
		return jobEmailRequestMap.get(jobName);
	}
	
	public void removeJobEmailRequests(String jobName){
		jobEmailRequestMap.remove(jobName);
	}
}
