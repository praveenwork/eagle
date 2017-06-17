package com.eagle.workflow.engine.service;

import com.eagle.contract.model.EagleEngineStopLimitRequest;

public interface StopLimitService {

	public boolean setupStopLimit(EagleEngineStopLimitRequest request);
}
