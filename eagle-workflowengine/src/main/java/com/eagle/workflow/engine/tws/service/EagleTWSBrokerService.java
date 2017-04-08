package com.eagle.workflow.engine.tws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eagle.contract.model.Instrument;
import com.eagle.workflow.engine.tws.api.EagleAPI;

/**
 * @author ppasupuleti
 *
 */
@Service
public class EagleTWSBrokerService {
	
	@Autowired
	private EagleAPI eagleAPI;
	
	public Instrument getInstrumentDetails(String instrumentName) {
		eagleAPI.checkAndConnect();
		//eagleAPI.reqHistoricalData(contract, endDateTime, duration, durationUnit, barSize, whatToShow, rthOnly, handler);
		return null;
	}
}
