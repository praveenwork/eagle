/**
 * 
 */
package com.eagle.contract.service;

import com.eagle.contract.model.Instrument;

/**
 * @author ppasupuleti
 *
 */
public interface BrokerHistoricalDataService {
	public void extractHistoricalData(Instrument instrument, int duration);
}
