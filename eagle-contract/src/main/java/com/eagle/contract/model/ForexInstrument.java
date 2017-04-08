package com.eagle.contract.model;

public class ForexInstrument extends Instrument {

	private static final long serialVersionUID = 1L;
	
	private InstrumentType 	instrumentType = InstrumentType.FOREX;

    @Override
	public InstrumentType getInstrumentType() {
		return instrumentType;
	}
}
