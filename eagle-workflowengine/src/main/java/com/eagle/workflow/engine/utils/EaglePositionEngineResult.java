package com.eagle.workflow.engine.utils;

import com.eagle.contract.constants.InstrumentPosition;

/**
 * @author ppasupuleti
 *
 */
public class EaglePositionEngineResult {
	
	private InstrumentPosition position;
	
	private int contractCount;

	public InstrumentPosition getPosition() {
		return position;
	}

	public void setPosition(InstrumentPosition position) {
		this.position = position;
	}

	public int getContractCount() {
		return contractCount;
	}

	public void setContractCount(int contractCount) {
		this.contractCount = contractCount;
	}
}
