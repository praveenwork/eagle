package com.eagle.contract.model;

//http://interactivebrokers.github.io/tws-api/basic_contracts.html#cash&gsc.tab=0
//http://interactivebrokers.github.io/tws-api/basic_orders.html#gsc.tab=0
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
