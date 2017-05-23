package com.eagle.workflow.engine.utils;

import com.eagle.contract.constants.InstrumentPosition;

/**
 * @author ppasupuleti
 *
 */
public class EaglePositionEngineResult {
	
	private InstrumentPosition position;
	
	private int sharesCount;

	public InstrumentPosition getPosition() {
		return position;
	}

	public void setPosition(InstrumentPosition position) {
		this.position = position;
	}

	public int getSharesCount() {
		return sharesCount;
	}

	public void setSharesCount(int sharesCount) {
		this.sharesCount = sharesCount;
	}
}
