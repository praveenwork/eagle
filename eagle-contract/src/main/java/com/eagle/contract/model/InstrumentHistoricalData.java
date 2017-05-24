package com.eagle.contract.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.eagle.contract.constants.EagleContractConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class InstrumentHistoricalData extends StoreData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
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
		StringBuilder data = new StringBuilder("InstrumentHistoricalData [");
		if(instrument!=null){
			data.append("instrument=" + instrument.getSymbol());
		}
		data.append("id = "+id).append(",");
		data.append("open = "+open).append(",");
		data.append("high = "+high).append(",");
		data.append("low = "+low).append(",");
		data.append("close = "+close).append(",");
		data.append("wap = "+wap).append(",");
		data.append("date = "+date).append(",");
		data.append("volume = "+volume).append(",");
		data.append("count = "+count).append(",");
		data.append("hasGaps = "+hasGaps);
		data.append("]");
		return data.toString();
	}

	@Override
	@JsonIgnore
	public CellProcessor[] getCellProcessor() {
		final CellProcessor[] processors = new CellProcessor[] { 
				new Optional(new ParseInt()), // ID
        		new Optional(), // Date
                new Optional(new ParseDouble()), // open
                new Optional(new ParseDouble()), // high
                new Optional(new ParseDouble()), // close
                new Optional(new ParseDouble()), // low
                new Optional(new ParseLong()), // volume
                new Optional(new ParseDouble()), // AdjClose
        };
        return processors;
	}
}
