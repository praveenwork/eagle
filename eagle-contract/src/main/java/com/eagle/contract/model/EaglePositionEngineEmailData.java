package com.eagle.contract.model;

import com.eagle.contract.model.InstrumentPositionState;

/**
 * @author ppasupuleti
 *
 */
public class EaglePositionEngineEmailData {
	
	private String symbol;
	
	private InstrumentPredictionData predictionData;
	
	private InstrumentPositionState todayPosition;
	
	private InstrumentPositionState nextDayPosition;
	
	private int IBPositions;
	
	private int leverageFactor;
	
	private EaglePositionEngineResult eaglePositionEngineResult;
	
	private boolean orderSubmited;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public InstrumentPredictionData getPredictionData() {
		return predictionData;
	}

	public void setPredictionData(InstrumentPredictionData predictionData) {
		this.predictionData = predictionData;
	}

	public InstrumentPositionState getTodayPosition() {
		return todayPosition;
	}

	public void setTodayPosition(InstrumentPositionState todayPosition) {
		this.todayPosition = todayPosition;
	}

	public int getIBPositions() {
		return IBPositions;
	}

	public void setIBPositions(int iBPositions) {
		IBPositions = iBPositions;
	}

	public int getLeverageFactor() {
		return leverageFactor;
	}

	public void setLeverageFactor(int leverageFactor) {
		this.leverageFactor = leverageFactor;
	}

	public EaglePositionEngineResult getEaglePositionEngineResult() {
		return eaglePositionEngineResult;
	}

	public void setEaglePositionEngineResult(EaglePositionEngineResult eaglePositionEngineResult) {
		this.eaglePositionEngineResult = eaglePositionEngineResult;
	}

	public boolean isOrderSubmited() {
		return orderSubmited;
	}

	public void setOrderSubmited(boolean orderSubmited) {
		this.orderSubmited = orderSubmited;
	}
	
	public InstrumentPositionState getNextDayPosition() {
		return nextDayPosition;
	}

	public void setNextDayPosition(InstrumentPositionState nextDayPosition) {
		this.nextDayPosition = nextDayPosition;
	}

	public String getEmailContent(){
		StringBuilder emailContent = new StringBuilder("");
		emailContent.append("<table>");
		emailContent.append("<tr><td><b>Instrument</b> 	:</td><td>"+this.getSymbol()).append("</td></tr>");
		emailContent.append("<tr><td><b>Position Engine</b> 	:</td><td>&nbsp;</td></tr>");
		emailContent.append("<tr><td><b>&emsp;Pridiction - nextdretPredicted</b> 		:</td><td>"+predictionData.getNextdretPredicted()).append("</td></tr>");
		emailContent.append("<tr><td><b>&emsp;Pridiction - nextdretPredictedLabels</b> 		:</td><td>"+predictionData.getNextdretPredictedLabels()).append("</td></tr>");
		emailContent.append("<tr><td><b>&emsp;Pridiction - nextdretRelativeProbability</b> 		:</td><td>"+predictionData.getNextdretRelativeProbability()).append("</td></tr>");
		emailContent.append("<tr><td><b>&emsp;NextDay Desired Position based on Prediction</b> 		:</td><td>"+this.getNextDayPosition().name()).append("</td></tr>");
		emailContent.append("<tr><td><b>&emsp;Open Positions From IB</b> 		:</td><td>"+this.getIBPositions()).append("</td></tr>");
		emailContent.append("<tr><td><b>&emsp;LeverageFactor</b>		:</td><td>"+this.getLeverageFactor()).append("</td></tr>");
		emailContent.append("<tr><td><b>&emsp;Today Position (after Position Engine)</b> 	:</td><td>"+this.getTodayPosition().name()).append("</td></tr>");

		String textColor = "green";
		if (this.getEaglePositionEngineResult().getPosition() == InstrumentPositionState.DO_NOTHING) {
			textColor = "brown";
		} else if (this.getEaglePositionEngineResult().getPosition() == InstrumentPositionState.BUY) {
			textColor = "green";
		} else {
			textColor = "red";
		}
		emailContent.append("<tr><td><b>Order Manager</b> 	:</td><td>&nbsp;</td></tr>");
		emailContent.append("<tr><td><b>&emsp;Order Position</b> 	:</td><td><font color=\""+textColor+"\">"+this.getEaglePositionEngineResult().getPosition()).append("</font></td></tr>");
		if (this.getEaglePositionEngineResult().getPosition() != InstrumentPositionState.DO_NOTHING) {
			emailContent.append("<tr><td><b>&emsp;Order Position - Limit Price</b> 	:</td><td>"+this.getEaglePositionEngineResult().getLimitPrice()).append("</td></tr>");
			emailContent.append("<tr><td><b>&emsp;Order Position - Contract Count</b> 	:</td><td>"+this.getEaglePositionEngineResult().getContractCount()).append("</td></tr>");
			emailContent.append("<tr><td><b>&emsp;Order Position - Stop Price</b> 	:</td><td>"+this.getEaglePositionEngineResult().getStopPrice()).append("</td></tr>");
			emailContent.append("<tr><td><b>&emsp;Order Position - Stop Limit Price</b> 	:</td><td>"+this.getEaglePositionEngineResult().getStopLimitPrice()).append("</td></tr>");
		}
		emailContent.append("</table>");
		emailContent.append("<br> <br>");
		
		return emailContent.toString();
	}
}
