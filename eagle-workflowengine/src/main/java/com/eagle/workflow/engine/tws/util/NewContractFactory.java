package com.eagle.workflow.engine.tws.util;

import com.eagle.contract.model.FuturesInstrument;
import com.eagle.contract.model.Instrument;
import com.ib.controller.NewContract;
import com.ib.controller.Types.SecType;

/**
 * @author ppasupuleti
 *
 */
public class NewContractFactory {

	public static NewContract getNewContract(Instrument instrument){
		NewContract newContract = new NewContract();
		newContract.currency(instrument.getCurrency());
		newContract.exchange(instrument.getExchange());
		newContract.primaryExch(instrument.getPrimaryExchange());
		newContract.symbol(instrument.getSymbol());
		newContract.expiry(instrument.getExpiry());
		newContract.secType(SecType.valueOf(instrument.getInstrumentType().getSecurityType()));
		return newContract;
	}
	
	public static Instrument getInstrument(NewContract newContract){
		FuturesInstrument instrument = new FuturesInstrument();
		instrument.setCurrency(newContract.currency());
		instrument.setExchange(newContract.exchange());
		instrument.setPrimaryExchange(newContract.primaryExch());
		instrument.setSymbol(newContract.symbol());
		instrument.setExpiry(newContract.expiry());
		return instrument;
	}
}
