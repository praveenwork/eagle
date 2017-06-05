package com.eagle.workflow.engine.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

/**
 * @author ppasupuleti
 *
 */
@Component
public class CancelOrderDataJobRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderDataJobRepository.class);
	
	private static ConcurrentHashMap<String, JobStatus> cancelOrderJobMap = new ConcurrentHashMap<>();
	
	public void addJob(String jobId, JobStatus jobStatus){
		cancelOrderJobMap.put(jobId, jobStatus);
	}
	
	public JobStatus getJobStatus(String jobId){
		return cancelOrderJobMap.get(jobId);
	}
	
	public void updateStatus(String jobId, JobStatus jobStatus){
		cancelOrderJobMap.put(jobId, jobStatus);
	}
	
	public void removeJob(String jobId){
		cancelOrderJobMap.remove(jobId);
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
		for (Map.Entry<String, JobStatus> mapEntry : cancelOrderJobMap.entrySet()) {
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
