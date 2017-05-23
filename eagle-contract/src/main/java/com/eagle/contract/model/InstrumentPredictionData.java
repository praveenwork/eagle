package com.eagle.contract.model;

import java.io.Serializable;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.eagle.contract.constants.EagleContractConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ppasupuleti
 *
 */
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class InstrumentPredictionData extends StoreData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_NEXTDRET_PREDICTED)
	private double nextdretPredicted;
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_NEXTDRET_PREDICTED_LABELS) 
	private String nextdretPredictedLabels = "";
	
	@JsonProperty(EagleContractConstants.INSTRUMENT_PREDICTION_NEXTDRET_RELATIVE_PROBABILITY)
	private double nextdretRelativeProbability;
	
	public InstrumentPredictionData() {
	}
	public double getNextdretPredicted() {
		return nextdretPredicted;
	}
	public void setNextdretPredicted(double nextdretPredicted) {
		this.nextdretPredicted = nextdretPredicted;
	}
	public String getNextdretPredictedLabels() {
		return nextdretPredictedLabels;
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

	@JsonIgnore
	public CellProcessor[] getCellProcessor() {
		final CellProcessor[] processors = new CellProcessor[] { 
                new Optional(new ParseDouble()), // nextdret_predicted
                new NotNull(), // nextdret_predicted_labels
                new Optional(new ParseDouble()), // nextdret_relative_probability
        };
        return processors;
	}
	
	@Override
	public String toString() {
		return "InstrumentPredictionData [nextdretPredicted=" + nextdretPredicted + ", nextdretPredictedLabels="
				+ nextdretPredictedLabels + ", nextdretRelativeProbability=" + nextdretRelativeProbability + "]";
	}
	
}