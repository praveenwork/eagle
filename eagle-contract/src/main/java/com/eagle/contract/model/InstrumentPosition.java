package com.eagle.contract.model;

/**
 * @author ppasupuleti
 *
 */
public class InstrumentPosition {
	private Instrument instrument;
	private String account;
	private double position;
	private double marketPrice;
	private double marketValue;
	private double averageCost;
	private double unrealPnl;
	private double realPnl;
	public Instrument getInstrument() {
		return instrument;
	}
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public double getPosition() {
		return position;
	}
	public void setPosition(double position) {
		this.position = position;
	}
	public double getMarketPrice() {
		return marketPrice;
	}
	public void setMarketPrice(double marketPrice) {
		this.marketPrice = marketPrice;
	}
	public double getMarketValue() {
		return marketValue;
	}
	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}
	public double getAverageCost() {
		return averageCost;
	}
	public void setAverageCost(double averageCost) {
		this.averageCost = averageCost;
	}
	public double getUnrealPnl() {
		return unrealPnl;
	}
	public void setUnrealPnl(double unrealPnl) {
		this.unrealPnl = unrealPnl;
	}
	public double getRealPnl() {
		return realPnl;
	}
	public void setRealPnl(double realPnl) {
		this.realPnl = realPnl;
	}
}
