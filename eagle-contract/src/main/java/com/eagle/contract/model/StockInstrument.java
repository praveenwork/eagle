package com.eagle.contract.model;

public class StockInstrument extends Instrument {

	private static final long serialVersionUID = 1L;
	
	private InstrumentType 	instrumentType = InstrumentType.STOCK;

    @Override
	public InstrumentType getInstrumentType() {
		return instrumentType;
	}
}
