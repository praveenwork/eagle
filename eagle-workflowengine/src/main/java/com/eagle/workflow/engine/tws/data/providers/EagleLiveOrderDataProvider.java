package com.eagle.workflow.engine.tws.data.providers;

import com.eagle.workflow.engine.repository.CancelOrderDataJobRepository;
import com.eagle.workflow.engine.repository.JobStatus;
import com.ib.controller.ApiController.ILiveOrderHandler;
import com.ib.controller.NewContract;
import com.ib.controller.NewOrder;
import com.ib.controller.NewOrderState;
import com.ib.controller.OrderStatus;

public class EagleLiveOrderDataProvider implements ILiveOrderHandler {
	

	private CancelOrderDataJobRepository cancelOrderDataJobRepository;

	public EagleLiveOrderDataProvider(CancelOrderDataJobRepository cancelOrderDataJobRepository) {
		this.cancelOrderDataJobRepository = cancelOrderDataJobRepository;
	}

	// IliveOrderHandler Methods:
	@Override
	public void handle(int orderId, int errorCode, String errorMsg) {
		System.out.println("In Live Order Handle mathod " + orderId + "   " + errorCode + "   " + errorMsg);
		cancelOrderDataJobRepository.updateStatus("CANCEL_ALL_ORDERS", JobStatus.FAILED);
	}

	@Override
	public void openOrder(NewContract contract, NewOrder order, NewOrderState orderState) {
		System.out.println("CancelOrders - In Live open Order mathod");
	}

	@Override
	public void openOrderEnd() {
		System.out.println("CancelOrders - In Live open Order End mathod");
		cancelOrderDataJobRepository.updateStatus("CANCEL_ALL_ORDERS", JobStatus.COMPLETED);
		
	}

	@Override
	public void orderStatus(int orderId, OrderStatus status, int filled, int remaining, double avgFillPrice,
			long permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
		System.out.println("CancelOrders - In Live order Status mathod");
	}
}
