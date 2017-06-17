package com.eagle.workflow.engine.tws.data.providers;

import com.eagle.contract.model.EaglePositionEngineResult;
import com.eagle.contract.model.Instrument;
import com.eagle.workflow.engine.repository.JobStatus;
import com.eagle.workflow.engine.repository.PlaceOrderDataJobRepository;
import com.ib.controller.ApiController.IOrderHandler;
import com.ib.controller.NewOrderState;
import com.ib.controller.OrderStatus;

public class EagleOrderDataProvider implements IOrderHandler {

	private PlaceOrderDataJobRepository placeOrderDataJobRepository;
	private Instrument instrument;
	
	
	public EagleOrderDataProvider(Instrument instrument, PlaceOrderDataJobRepository placeOrderDataJobRepository) {
		this.placeOrderDataJobRepository = placeOrderDataJobRepository;
		this.instrument = instrument;
	}

	@Override
	public void handle(int errorCode, String errorMsg) {
		System.out.println("In order handle mathod "+errorCode +" - "+errorMsg);
		placeOrderDataJobRepository.updateStatus(this.instrument.getSymbol(), JobStatus.FAILED);
	}

	@Override
	public void orderState(NewOrderState orderState) {
		System.out.println("In order State mathod "+orderState.toString());
		placeOrderDataJobRepository.updateStatus(this.instrument.getSymbol(), JobStatus.COMPLETED);
	}

	@Override
	public void orderStatus(OrderStatus status, int filled, int remaining, double avgFillPrice, long permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
		System.out.println("In order State mathod "+ status.toString() + " - filled -"+filled + "-  remaining - "+ remaining + " - avgFillPrice -"+avgFillPrice);
		placeOrderDataJobRepository.updateStatus(this.instrument.getSymbol(), JobStatus.COMPLETED);
	}
}
