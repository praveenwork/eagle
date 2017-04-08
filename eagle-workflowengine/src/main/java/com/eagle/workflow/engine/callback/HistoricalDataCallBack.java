package com.eagle.workflow.engine.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.eagle.contract.model.InstrumentHistoricalData;


public class HistoricalDataCallBack implements ListenableFutureCallback<InstrumentHistoricalData>{
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoricalDataCallBack.class);
	
	private final HistoritialDataResultFuture future;
	
	public HistoricalDataCallBack(HistoritialDataResultFuture future) {
		this.future = future;
	}

	@Override
	public void onSuccess(InstrumentHistoricalData historicalData) {
		LOGGER.debug("===== On Success ======");
		future.getHistoritialDataResultFuture().set(historicalData);
	}

	@Override
	public void onFailure(Throwable ex) {
		LOGGER.debug("===== On Failure ======");
		future.getHistoritialDataResultFuture().set(null);
	}
}
