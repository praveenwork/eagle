package com.eagle.workflow.engine.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.eagle.contract.model.EagleEngineStopLimitRequest;
import com.eagle.workflow.engine.service.StopLimitService;

/**
 * @author ppasupuleti
 *
 */
@Component
public class PlaceOrderDataJobRepository {
	
	@Autowired
	private StopLimitService stopLimitService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PlaceOrderDataJobRepository.class);
	
	private static ConcurrentHashMap<String, JobStatus> placeOrderJobMap = new ConcurrentHashMap<>();
	
	private static ConcurrentHashMap<String, EagleEngineStopLimitRequest> stopOrderJobDetailsMap = new ConcurrentHashMap<>();
	
	public void addJob(String jobId, JobStatus jobStatus){
		placeOrderJobMap.put(jobId, jobStatus);
	}
	
	public void addJobDetails(String jobId, EagleEngineStopLimitRequest request){
		stopOrderJobDetailsMap.put(jobId, request);
	}
	
	public JobStatus getJobStatus(String jobId){
		return placeOrderJobMap.get(jobId);
	}
	
	public EagleEngineStopLimitRequest getJobDetails(String jobId){
		return stopOrderJobDetailsMap.get(jobId);
	}
	
	public void updateStatus(String jobId, JobStatus jobStatus){
		placeOrderJobMap.put(jobId, jobStatus);
		if (jobStatus.name().equals(JobStatus.COMPLETED)) {
			//Call StopLimit
			stopLimitService.setupStopLimit(stopOrderJobDetailsMap.get(jobId));
		}
		
	}
	
	public void removeJobDetails(String jobId){
		placeOrderJobMap.remove(jobId);
	}
	
	public void removeJob(String jobId){
		stopOrderJobDetailsMap.remove(jobId);
	}
	
	
	public ListenableFuture<Boolean> isJobsDone(){
		SettableListenableFuture<Boolean> jobFuture = new SettableListenableFuture<>();
		boolean result = true;
		while (!jobFuture.isDone()) {
			result = checkJobsStatus();
			if (result) {
				jobFuture.set(true);
			}
		}
		LOGGER.debug("isJobDone method.. Returing:Result:"+result);
		return jobFuture;
	}
	
	public ListenableFuture<Boolean> isJobsDone(String instrumentSymbol){
		SettableListenableFuture<Boolean> jobFuture = new SettableListenableFuture<>();
		boolean result = true;
		while (!jobFuture.isDone()) {
			result = checkJobsStatus();
			if (result) {
				jobFuture.set(true);
			}
		}
		LOGGER.debug("isJobDone method.. Returing:Result:"+result);
		return jobFuture;
	}
	
	private boolean checkJobsStatus() {
		boolean result = true;
		for (Map.Entry<String, JobStatus> mapEntry : placeOrderJobMap.entrySet()) {
			if (mapEntry.getValue() != JobStatus.COMPLETED) {
				result = false;
				break;
			}
			if (mapEntry.getValue() == JobStatus.COMPLETED) {
				removeJob(mapEntry.getKey());
			}
		}
		return result;
	}
}
