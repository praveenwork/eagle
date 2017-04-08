package com.eagle.contract.model;

public enum InstrumentType {
	STOCK("STK"), 
	FUTURES("FUT"), 
	OPTION("OPT"), 
	FOREX("CASH"), 
	INDEX("IND"), 
	COMBO("BAG"), 
	CFD("CFD");
	
	String securityType;

	private InstrumentType(String securityType) {
		this.securityType = securityType;
	}

	public String getSecurityType() {
		return securityType;
	}
}
