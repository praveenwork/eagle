package com.eagle.contract.model;

//@JsonInclude(JsonInclude.Include.NON_EMPTY)
//@JsonIgnoreProperties(ignoreUnknown=true)
public class InstrumentRealTimeData {

	private static final long serialVersionUID = 1L;
	
	private Double bid;
    private Double ask;
    private Double last;
    private Double close;
    
    private Boolean frozen;
    private Integer bidSize;
    private Integer askSize;
    private Long lastTime;
    
    private Integer volume;
    
    private Long timestamp;
    private String timestampFormatted;
    
	public Double getBid() {
		return bid;
	}


	public void setBid(Double bid) {
		this.bid = bid;
	}


	public Double getAsk() {
		return ask;
	}


	public void setAsk(Double ask) {
		this.ask = ask;
	}


	public Double getLast() {
		return last;
	}


	public void setLast(Double last) {
		this.last = last;
	}

	public Boolean getFrozen() {
		return frozen;
	}


	public void setFrozen(Boolean frozen) {
		this.frozen = frozen;
	}


	public Integer getBidSize() {
		return bidSize;
	}


	public void setBidSize(Integer bidSize) {
		this.bidSize = bidSize;
	}


	public Integer getAskSize() {
		return askSize;
	}


	public void setAskSize(Integer askSize) {
		this.askSize = askSize;
	}


	public Long getLastTime() {
		return lastTime;
	}


	public void setLastTime(Long lastTime) {
		this.lastTime = lastTime;
	}


	public Long getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}


	public String getTimestampFormatted() {
		return timestampFormatted;
	}


	public void setTimestampFormatted(String timestampFormatted) {
		this.timestampFormatted = timestampFormatted;
	}


	public Double getClose() {
		return close;
	}


	public void setClose(Double close) {
		this.close = close;
	}


	public Integer getVolume() {
		return volume;
	}


	public void setVolume(Integer volume) {
		this.volume = volume;
	}


	@Override
	public String toString() {
		return "InstrumentRealTimeData [bid=" + bid + ", ask=" + ask + ", last=" + last + ", close=" + close
				+ ", frozen=" + frozen + ", bidSize=" + bidSize + ", askSize=" + askSize + ", lastTime=" + lastTime
				+ ", volume=" + volume + ", timestamp=" + timestamp + ", timestampFormatted=" + timestampFormatted
				+ "]";
	}
}
