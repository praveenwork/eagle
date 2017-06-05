package com.eagle.contract.service;

import java.util.List;

import com.eagle.contract.model.EaglePositionEngineResult;
import com.eagle.contract.model.Instrument;

public interface BrokerService extends BrokerHistoricalDataService{
	
	public void getPortifolioPosition(Instrument instrument, String accountCode);
	
	public List<String> getAccounts();
	
	public double getCurrentInstrumentPrice(Instrument instrument);
	
	public void getInstrumentOpenPosition(Instrument instrument);
	
	public void cancelAllOpenOrders();
	
	public void placeOrder(Instrument instrument, EaglePositionEngineResult positionEngineResult, String account);
	
	public void setStopLimit(Instrument instrument);
	
}
