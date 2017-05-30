package com.eagle.workflow.engine.store;

import java.util.List;

import com.eagle.contract.model.InstrumentHistoricalData;

/**
 * @author ppasupuleti
 *
 */
public interface InstrumentStoreService {
	
	public Boolean storeRawData(InstrumentHistoricalData historicalData);
	
	public void storeRawData(List<InstrumentHistoricalData> historicalDataList);
}
