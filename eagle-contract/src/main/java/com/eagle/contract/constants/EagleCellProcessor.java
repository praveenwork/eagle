/**
 * 
 */
package com.eagle.contract.constants;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * @author ppasupuleti
 *
 */
public enum EagleCellProcessor {
	
	//Instrument Historical Data
	INSTRUMENT_HISTORICAL_ID(EagleContractConstants.INSTRUMENT_HISTORICAL_ID,new Optional(new ParseInt())),
	INSTRUMENT_HISTORICAL_DATE(EagleContractConstants.INSTRUMENT_HISTORICAL_DATE,new Optional()),
	INSTRUMENT_HISTORICAL_OPEN(EagleContractConstants.INSTRUMENT_HISTORICAL_OPEN,new Optional(new ParseDouble())),
	INSTRUMENT_HISTORICAL_CLOSE(EagleContractConstants.INSTRUMENT_HISTORICAL_CLOSE,new Optional(new ParseDouble())),
	INSTRUMENT_HISTORICAL_LOW(EagleContractConstants.INSTRUMENT_HISTORICAL_LOW,new Optional(new ParseDouble())),
	INSTRUMENT_HISTORICAL_HIGH(EagleContractConstants.INSTRUMENT_HISTORICAL_HIGH,new Optional(new ParseDouble())),
	INSTRUMENT_HISTORICAL_VOLUME(EagleContractConstants.INSTRUMENT_HISTORICAL_VOLUME,new Optional(new ParseLong())),
	INSTRUMENT_HISTORICAL_ADJCLOSE(EagleContractConstants.INSTRUMENT_HISTORICAL_ADJCLOSE,new Optional(new ParseDouble())),
	
	//Instrument Predection
	INSTRUMENT_PREDICTION_NEXTDRET_PREDICTED(EagleContractConstants.INSTRUMENT_PREDICTION_NEXTDRET_PREDICTED,new Optional(new ParseDouble())),
	INSTRUMENT_PREDICTION_NEXTDRET_PREDICTED_LABELS(EagleContractConstants.INSTRUMENT_PREDICTION_NEXTDRET_PREDICTED_LABELS, new NotNull()),
	INSTRUMENT_PREDICTION_NEXTDRET_RELATIVE_PROBABILITY(EagleContractConstants.INSTRUMENT_PREDICTION_NEXTDRET_RELATIVE_PROBABILITY,new Optional(new ParseDouble())),
	;
	
	private String columnName;
	private CellProcessor columnCellProcessor;
	private EagleCellProcessor(String columnName, CellProcessor columnCellProcessor) {
		this.columnName = columnName;
		this.columnCellProcessor = columnCellProcessor;
	}
	public String getColumnName() {
		return columnName;
	}
	public CellProcessor getColumnCellProcessor() {
		return columnCellProcessor;
	}
	
	
	public static CellProcessor getCellProcessor(String columneName){
		EagleCellProcessor[] cellProessors = EagleCellProcessor.values();
		for (EagleCellProcessor eagleCellProcessor : cellProessors) {
			if(columneName.equalsIgnoreCase(eagleCellProcessor.getColumnName())){
				return eagleCellProcessor.getColumnCellProcessor();
			}
		}
		return null;
	}
}
