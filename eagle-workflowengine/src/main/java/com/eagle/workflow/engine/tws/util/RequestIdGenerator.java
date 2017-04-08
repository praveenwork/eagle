package com.eagle.workflow.engine.tws.util;

public class RequestIdGenerator {
	
	private static int nextRequestId;
	
	public static int getNextReqId(){
		if(nextRequestId == 0){
			nextRequestId = 1;
		} else {
			nextRequestId ++;
		}
		return nextRequestId;
	}
}
