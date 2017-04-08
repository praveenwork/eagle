package com.eagle.contract.model;

public class OptionsInstrument extends Instrument {

	private static final long serialVersionUID = 1L;
	
	private InstrumentType 	instrumentType = InstrumentType.OPTION;

    @Override
	public InstrumentType getInstrumentType() {
		return instrumentType;
	}
}
