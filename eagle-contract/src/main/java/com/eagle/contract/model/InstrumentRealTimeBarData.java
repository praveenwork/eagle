package com.eagle.contract.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class InstrumentRealTimeBarData extends InstrumentHistoricalData {

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "InstrumentRealTimeBarData [getOpen()=" + getOpen() + ", getHigh()=" + getHigh() + ", getLow()="
				+ getLow() + ", getClose()=" + getClose() + ", getWap()=" + getWap() + ", getDate()=" + getDate()
				+ ", getVolume()=" + getVolume() + ", getCount()=" + getCount() + ", isHasGaps()=" + isHasGaps()
				+ ", getInstrument()=" + getInstrument() + ", toString()=" + super.toString() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + "]";
	}
}
