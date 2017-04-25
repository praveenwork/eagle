package com.eagle.contract.service;

import com.eagle.contract.model.Instrument;

public interface BrokerService {
	
	/**
	 * @param instrument
	 */
	public void extractHistoricalData(Instrument instrument, int duration);
	
}
