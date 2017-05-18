package com.eagle.contract.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.eagle.contract.constants.EagleContractConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ppasupuleti
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class InstrumentPredictionData extends StoreData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_ID)
	private long id;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_DATE)
	private String date;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_CLOSE)
	private double close;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_CLOSE_BY_HIGH_MIN95)
	private double cloByHighMin95;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_CLOSE_BY_LOW_MIN95)
	private double cloByLowMin95;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_ONE_DR)
	private double oneDr;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_ONE_DR_CAT)
	private String oneDrCat;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_TWO_DR)
	private double twoDr;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_TWO_DR_CAT)
	private String twoDrCat;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_THREE_DR)
	private double threeDr;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_THREE_DR_CAT)
	private String threeDrCat;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_FIVE_DR)
	private double fiveDr;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_FIVE_DR_CAT)
	private String fiveDrCat;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_MACD_531)
	private double macd531;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_MACD_532)
	private double macd532;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_RSI_3)
	private double rsi3;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_RSI_3_CATEGORY)
	private String rsi3Category;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_STOCK_3K)
	private double stoc3k;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_STOCK_3K_CAT)
	private String stock3kCat;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_STOCK_7KD)
	private double stock7kd;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_CCI3)
	private double cci3;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_CCI5)
	private double cci5;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_NEXTDRET)
	private double nextdret;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_NEXTDRET_PREDICTED)
	private double nextdretPredicted;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_NEXTDRET_PREDICTED_LABELS)
	private String nextdretPredictedLabels;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_NEXTDRET_RELATIVE_PROBABILITY)
	private double nextdretRelativeProbability;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getCloByHighMin95() {
		return cloByHighMin95;
	}
	public void setCloByHighMin95(double cloByHighMin95) {
		this.cloByHighMin95 = cloByHighMin95;
	}
	public double getCloByLowMin95() {
		return cloByLowMin95;
	}
	public void setCloByLowMin95(double cloByLowMin95) {
		this.cloByLowMin95 = cloByLowMin95;
	}
	public double getOneDr() {
		return oneDr;
	}
	public void setOneDr(double oneDr) {
		this.oneDr = oneDr;
	}
	public String getOneDrCat() {
		return oneDrCat == null ? " " : oneDrCat;
	}
	public void setOneDrCat(String oneDrCat) {
		this.oneDrCat = oneDrCat;
	}
	public double getTwoDr() {
		return twoDr;
	}
	public void setTwoDr(double twoDr) {
		this.twoDr = twoDr;
	}
	public String getTwoDrCat() {
		return twoDrCat == null ? " " : twoDrCat;
	}
	public void setTwoDrCat(String twoDrCat) {
		this.twoDrCat = twoDrCat;
	}
	public double getThreeDr() {
		return threeDr;
	}
	public void setThreeDr(double threeDr) {
		this.threeDr = threeDr;
	}
	public String getThreeDrCat() {
		return threeDrCat == null ? " " : threeDrCat;
	}
	public void setThreeDrCat(String threeDrCat) {
		this.threeDrCat = threeDrCat;
	}
	public double getFiveDr() {
		return fiveDr;
	}
	public void setFiveDr(double fiveDr) {
		this.fiveDr = fiveDr;
	}
	public String getFiveDrCat() {
		return fiveDrCat == null ? " " : fiveDrCat;
	}
	public void setFiveDrCat(String fiveDrCat) {
		this.fiveDrCat = fiveDrCat;
	}
	public double getMacd531() {
		return macd531;
	}
	public void setMacd531(double macd531) {
		this.macd531 = macd531;
	}
	public double getMacd532() {
		return macd532;
	}
	public void setMacd532(double macd532) {
		this.macd532 = macd532;
	}
	public double getRsi3() {
		return rsi3;
	}
	public void setRsi3(double rsi3) {
		this.rsi3 = rsi3;
	}
	public String getRsi3Category() { 
		return rsi3Category== null ? " " : rsi3Category;
	}
	public void setRsi3Category(String rsi3Category) {
		this.rsi3Category = rsi3Category;
	}
	public double getStoc3k() {
		return stoc3k;
	}
	public void setStoc3k(double stoc3k) {
		this.stoc3k = stoc3k;
	}
	public String getStock3kCat() {
		return stock3kCat== null ? " " : stock3kCat;
	}
	public void setStock3kCat(String stock3kCat) {
		this.stock3kCat = stock3kCat;
	}
	public double getStock7kd() {
		return stock7kd;
	}
	public void setStock7kd(double stock7kd) {
		this.stock7kd = stock7kd;
	}
	public double getCci3() {
		return cci3;
	}
	public void setCci3(double cci3) {
		this.cci3 = cci3;
	}
	public double getCci5() {
		return cci5;
	}
	public void setCci5(double cci5) {
		this.cci5 = cci5;
	}
	public double getNextdret() {
		return nextdret;
	}
	public void setNextdret(double nextdret) {
		this.nextdret = nextdret;
	}
	public double getNextdretPredicted() {
		return nextdretPredicted;
	}
	public void setNextdretPredicted(double nextdretPredicted) {
		this.nextdretPredicted = nextdretPredicted;
	}
	public String getNextdretPredictedLabels() {
		return nextdretPredictedLabels == null ? " " : nextdretPredictedLabels;
	}
	public void setNextdretPredictedLabels(String nextdretPredictedLabels) {
		this.nextdretPredictedLabels = nextdretPredictedLabels;
	}
	public double getNextdretRelativeProbability() {
		return nextdretRelativeProbability;
	}
	public void setNextdretRelativeProbability(double nextdretRelativeProbability) {
		this.nextdretRelativeProbability = nextdretRelativeProbability;
	}
	@Override
	@JsonIgnore
	public CellProcessor[] getCellProcessor() {
		final CellProcessor[] processors = new CellProcessor[] { 
				new Optional(new ParseLong()), // id
				new Optional(), // Date
                new Optional(new ParseDouble()), // close
                new Optional(new ParseDouble()), // clo_by_high_min95
                new Optional(new ParseDouble()), // close_by_low_min95
                new Optional(new ParseDouble()), // 1d_r
                new NotNull(), // 1d_r_cat
                new Optional(new ParseDouble()), // 2d_r
                new NotNull(), // 2d_r_cat
                new Optional(new ParseDouble()), // 3d_r
                new NotNull(), // 3d_r_cat
                new Optional(new ParseDouble()), // 5d_r
                new NotNull(), // 5d_r_cat
                new Optional(new ParseDouble()), // macd_5_3_1
                new Optional(new ParseDouble()), // macd_5_3_2
                new Optional(new ParseDouble()), // rsi_3
                new NotNull(), // rsi_3_category
                new Optional(new ParseDouble()), // stoc_3_k
                new NotNull(), // stock_3_k_cat
                new Optional(new ParseDouble()), // stock_7_k_d
                new Optional(new ParseDouble()), // cci_3
                new Optional(new ParseDouble()), // cci_5
                new Optional(new ParseDouble()), // nextdret
                new Optional(new ParseDouble()), // nextdret_predicted
                new NotNull(), // nextdret_predicted_labels
                new Optional(new ParseDouble()), // nextdret_relative_probability
        };
        return processors;
	}
	@Override
	public String toString() {
		return "InstrumentPredictionData [id=" + id + ", date=" + date + ", close=" + close + ", cloByHighMin95="
				+ cloByHighMin95 + ", cloByLowMin95=" + cloByLowMin95 + ", oneDr=" + oneDr + ", oneDrCat=" + oneDrCat
				+ ", twoDr=" + twoDr + ", twoDrCat=" + twoDrCat + ", threeDr=" + threeDr + ", threeDrCat=" + threeDrCat
				+ ", fiveDr=" + fiveDr + ", fiveDrCat=" + fiveDrCat + ", macd531=" + macd531 + ", macd532=" + macd532
				+ ", rsi3=" + rsi3 + ", rsi3Category=" + rsi3Category + ", stoc3k=" + stoc3k + ", stock3kCat="
				+ stock3kCat + ", stock7kd=" + stock7kd + ", cci3=" + cci3 + ", cci5=" + cci5 + ", nextdret=" + nextdret
				+ ", nextdretPredicted=" + nextdretPredicted + ", nextdretPredictedLabels=" + nextdretPredictedLabels
				+ ", nextdretRelativeProbability=" + nextdretRelativeProbability + "]";
	}
}