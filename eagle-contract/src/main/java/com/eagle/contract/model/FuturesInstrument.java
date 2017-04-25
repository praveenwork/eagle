package com.eagle.contract.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class FuturesInstrument extends Instrument {

	private static final long serialVersionUID = 1L;
	
	private InstrumentType 	instrumentType = InstrumentType.FUTURES;

    @Override
	public InstrumentType getInstrumentType() {
		return instrumentType;
	}
    
    
    /*
    @Override
	public String getExpiry() {
    	DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("YYYYMMdd");
		return LocalDate.now().plusDays(1).format(dateFormat); // expiry is 10 days
	}*/
}
