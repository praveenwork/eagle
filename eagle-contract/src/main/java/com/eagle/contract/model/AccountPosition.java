package com.eagle.contract.model;

/**
 * @author ppasupuleti
 *
 */
public class AccountPosition {

	private String accountName;
	private String positionAt;
	private int position;
	private double marketPrice;
	private double marketValue;
	private String contractDescription;
	private String timeStamp;
	
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getPositionAt() {
		return positionAt;
	}
	public void setPositionAt(String positionAt) {
		this.positionAt = positionAt;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
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
	public String getContractDescription() {
		return contractDescription;
	}           
	public void setContractDescription(String contractDescription) {
		this.contractDescription = contractDescription;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
}
