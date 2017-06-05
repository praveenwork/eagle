package com.eagle.contract.model;

import com.eagle.contract.model.InstrumentPositionState;

/**
 * @author ppasupuleti
 *
 */
public class EaglePositionEngineResult {
	
	private InstrumentPositionState position;
	
	private int contractCount;
	
	private double limitPrice;

	public InstrumentPositionState getPosition() {
		return position;
	}

	public void setPosition(InstrumentPositionState position) {
		this.position = position;
	}

	public int getContractCount() {
		return contractCount;
	}

	public void setContractCount(int contractCount) {
		this.contractCount = contractCount;
	}

	public double getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(double limitPrice) {
		this.limitPrice = limitPrice;
	}

	@Override
	public String toString() {
		return "EaglePositionEngineResult [position=" + position.name() + ", contractCount=" + contractCount + ", limitPrice="
				+ limitPrice + "]";
	}
}
