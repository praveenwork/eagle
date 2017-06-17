package com.eagle.workflow.engine.utils;

import org.springframework.stereotype.Component;

import com.eagle.contract.model.Instrument;
import com.eagle.contract.model.InstrumentPositionState;

@Component
public class EagleEnginePriceCalculator {
	
	public double getLimitPrice(Instrument instrument, InstrumentPositionState positionState, double currentPrice){
		if(positionState == InstrumentPositionState.BUY){
			return currentPrice + (instrument.getPriceLimit());
		} else {
			return currentPrice - (instrument.getPriceLimit());
		}
	}
	
	public double getStopLimitPrice(Instrument instrument, InstrumentPositionState positionState, double currentPrice){
		double stopLimitPrice = currentPrice;
		if(positionState == InstrumentPositionState.BUY){
			stopLimitPrice = (currentPrice
					* (1 + instrument.getStopTriggerPercentage() + instrument.getStopLimitPercentage()));
		} else {
			stopLimitPrice = (currentPrice
					* (1 + instrument.getStopTriggerPercentage() + instrument.getStopLimitPercentage()));
		}
		// round price
		return getRoundPrice(stopLimitPrice, instrument.getPriceRoundingUnit());
	}
	
	public double getStopPrice(Instrument instrument, InstrumentPositionState positionState, double currentPrice){
		double stopPrice = currentPrice;
		if (positionState == InstrumentPositionState.BUY) {
			stopPrice = (currentPrice * (1+instrument.getStopTriggerPercentage()));
		} else {
			stopPrice = (currentPrice * (1 -instrument.getStopTriggerPercentage()));
		}
		// round price
		return getRoundPrice(stopPrice, instrument.getPriceRoundingUnit());
	}
	
	//---------Helpers---------
	private double getRoundPrice(double price, double instrumentRoundUnit){
		double integralPart = Math.floor(price);
		double fractionalPart = price - integralPart;
		double roundUnitMidValue = instrumentRoundUnit/2;
		double gap = fractionalPart%instrumentRoundUnit;

		if(gap > roundUnitMidValue){
			fractionalPart  = fractionalPart + (instrumentRoundUnit - gap);
		} else {
			fractionalPart  = fractionalPart - gap;
		}
		return integralPart+fractionalPart;
	}
}
