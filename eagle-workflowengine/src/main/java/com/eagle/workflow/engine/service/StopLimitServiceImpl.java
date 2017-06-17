package com.eagle.workflow.engine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.model.EagleEngineStopLimitRequest;
import com.eagle.workflow.engine.tws.client.EagleTWSClient;

/**
 * @author ppasupuleti
 *
 */
@Component
public class StopLimitServiceImpl implements StopLimitService {

	
	@Autowired
	private EagleTWSClient eagleTWSClient;
	
	/* (non-Javadoc)
	 * @see com.eagle.workflow.engine.service.StopLimitService#setupStopLimit(com.eagle.workflow.engine.utils.EagleEngineStopLimitRequest)
	 */
	@Override
	public boolean setupStopLimit(EagleEngineStopLimitRequest request) {
		try {
			eagleTWSClient.placeOrder(request.getInstrument(), request.getPositionResult(), request.getAccountName());
			return true;
		} catch (EagleException e) {
			throw e;
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_SUBMIT_ORDER, request.getInstrument().getSymbol(), e.getMessage());
		}
	}

}
