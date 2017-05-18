/**
 * 
 */
package com.eagle.workflow.engine.store;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.eagle.contract.model.InstrumentHistoricalData;
import com.eagle.contract.model.InstrumentPredictionData;

/**
 * @author ppasupuleti
 *
 */
public enum CsvDataProcessor {
	
	INSTUMENT_HISTORICAL_DATA("com.eagle.contract.model.InstrumentHistoricalData", new InstrumentHistoricalData().getCellProcessor()),
	INSTUMENT_PREDICITON_DATA("com.eagle.contract.model.InstrumentPredictionData", new InstrumentPredictionData().getCellProcessor()),
	;
	String clazzName;
	CellProcessor[] processor;
	private CsvDataProcessor(String clazzName, CellProcessor[] processor) {
		this.clazzName = clazzName;
		this.processor = processor;
	}
	
	public String getClazzName() {
		return clazzName;
	}

	public CellProcessor[] getProcessor() {
		return processor;
	}

	public static CellProcessor[] getCellProcessor(String clazzName){
		CsvDataProcessor[] dataProcessors = CsvDataProcessor.values();
		for (CsvDataProcessor csvDataProcessor : dataProcessors) {
			if(csvDataProcessor.getClazzName().equalsIgnoreCase(clazzName)){
				return csvDataProcessor.getProcessor();
			}
		}
		return null;
	}
	
}
