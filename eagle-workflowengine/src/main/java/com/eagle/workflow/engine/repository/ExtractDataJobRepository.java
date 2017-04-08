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
public class ExtractDataJobRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtractDataJobRepository.class);
	
	private static ConcurrentHashMap<String, JobStatus> extractJobMap = new ConcurrentHashMap<>();
	
	
	
	public void addJob(String jobId, JobStatus jobStatus){
		extractJobMap.put(jobId, jobStatus);
	}
	
	public JobStatus getJobStatus(String jobId){
		return extractJobMap.get(jobId);
	}
	
	public void updateStatus(String jobId, JobStatus jobStatus){
		extractJobMap.put(jobId, jobStatus);
	}
	
	public void removeJob(String jobId){
		extractJobMap.remove(jobId);
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
	
	private boolean checkJobsStatus() {
		boolean result = true;
		for (Map.Entry<String, JobStatus> mapEntry : extractJobMap.entrySet()) {
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
