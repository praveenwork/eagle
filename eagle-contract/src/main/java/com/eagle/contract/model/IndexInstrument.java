package com.eagle.contract.model;

public class IndexInstrument extends Instrument {

	private static final long serialVersionUID = 1L;
	
	private InstrumentType 	instrumentType = InstrumentType.INDEX;

    @Override
	public InstrumentType getInstrumentType() {
		return instrumentType;
	}
}
