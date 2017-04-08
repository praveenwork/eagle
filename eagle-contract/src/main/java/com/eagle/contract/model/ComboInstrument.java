package com.eagle.contract.model;

public class ComboInstrument extends Instrument {

	private static final long serialVersionUID = 1L;
	
	private InstrumentType 	instrumentType = InstrumentType.COMBO;

    @Override
	public InstrumentType getInstrumentType() {
		return instrumentType;
	}
}
