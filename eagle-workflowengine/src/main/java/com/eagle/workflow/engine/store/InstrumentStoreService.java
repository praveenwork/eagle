package com.eagle.workflow.engine.store;

import com.eagle.contract.model.InstrumentHistoricalData;

/**
 * @author ppasupuleti
 *
 */
public interface InstrumentStoreService {
	
	public Boolean storeRawData(InstrumentHistoricalData historicalData);
}
