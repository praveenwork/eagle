package com.eagle.contract.model;

import com.eagle.contract.constants.EagleContractConstants;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CFDInstrument extends Instrument {

	private static final long serialVersionUID = 1L;
	
	private InstrumentType 	instrumentType = InstrumentType.CFD;

    @Override
	public InstrumentType getInstrumentType() {
		return instrumentType;
	}
}
