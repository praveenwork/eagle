package com.eagle.contract.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.eagle.contract.constants.EagleContractConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class InstrumentHistoricalData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	
	@JsonIgnore
	//@JsonProperty("Instrument")
	private Instrument instrument;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_HISTORICAL_ID)
	private int id;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_HISTORICAL_DATE)
	private String date;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_HISTORICAL_OPEN)
	private double open;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_HISTORICAL_CLOSE)
	private double close;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_HISTORICAL_LOW)
	private double low;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_HISTORICAL_HIGH)
	private double high;
	
	@JsonIgnore
	//@JsonProperty(EagleContractConstants.INSTRUMENT_WAP)
	private double wap;
	
	//@JsonIgnore
	@JsonProperty(EagleContractConstants.INSTRUMENT_HISTORICAL_VOLUME)
	private long volume;
	
	@JsonIgnore
	//@JsonProperty(EagleContractConstants.INSTRUMENT_COUNT)
	private int count;
	
	@JsonIgnore
	//@JsonProperty(EagleContractConstants.INSTRUMENT_HASGAPS)
	private boolean hasGaps;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_HISTORICAL_ADJCLOSE)
	private double adjClose;
	
	public InstrumentHistoricalData() {
	}
	
	public double getOpen(){
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getWap() {
		return wap;
	}
	public void setWap(double wap) {
		this.wap = wap;
	}
	public String getDate() {
		if(date == null){
			return LocalDateTime.now().format(dateFormat);
		}
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public boolean isHasGaps() {
		return hasGaps;
	}
	public void setHasGaps(boolean hasGaps) {
		this.hasGaps = hasGaps;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public double getAdjClose() {
		return adjClose;
	}

	public void setAdjClose(double adjClose) {
		this.adjClose = adjClose;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "InstrumentHistoricalData [instrument=" + instrument.getSymbol() + " id ="+id+", open=" + open + ", high=" + high + ", low="
				+ low + ", close=" + close + ", wap=" + wap + ", date=" + date + ", volume=" + volume + ", count="
				+ count + ", hasGaps=" + hasGaps + "]";
	}
}
