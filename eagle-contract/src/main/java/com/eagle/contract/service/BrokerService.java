package com.eagle.contract.service;

import java.util.List;

import com.eagle.contract.model.Instrument;

public interface BrokerService {
	
	/**
	 * @param instrument
	 */
	public void extractHistoricalData(Instrument instrument, int duration);
	
	public void getPortifolioPosition(Instrument instrument, String accountCode);
	
	public List<String> getAccounts();
}
