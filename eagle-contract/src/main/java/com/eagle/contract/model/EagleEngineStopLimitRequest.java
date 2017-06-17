package com.eagle.contract.model;

import com.eagle.contract.model.EaglePositionEngineResult;
import com.eagle.contract.model.Instrument;

/**
 * @author ppasupuleti
 *
 */
public class EagleEngineStopLimitRequest {

	private Instrument instrument;
	
	private EaglePositionEngineResult positionResult;
	
	private String accountName;

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public EaglePositionEngineResult getPositionResult() {
		return positionResult;
	}

	public void setPositionResult(EaglePositionEngineResult positionResult) {
		this.positionResult = positionResult;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
}