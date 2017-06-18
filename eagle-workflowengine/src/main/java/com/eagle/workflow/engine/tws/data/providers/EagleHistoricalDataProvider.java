package com.eagle.workflow.engine.tws.data.providers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eagle.contract.model.Instrument;
import com.eagle.contract.model.InstrumentHistoricalData;
import com.eagle.workflow.engine.repository.ExtractDataJobRepository;
import com.eagle.workflow.engine.repository.JobStatus;
import com.eagle.workflow.engine.store.InstrumentStoreService;
import com.ib.controller.ApiController.IHistoricalDataHandler;
import com.ib.controller.Bar;

@Component
public class EagleHistoricalDataProvider implements IHistoricalDataHandler{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EagleHistoricalDataProvider.class);
	
	private Instrument instrument;

	@Autowired
	private InstrumentStoreService instrumentStoreService;
	
	private ExtractDataJobRepository extractDataJobRepository;
	
	public EagleHistoricalDataProvider(){
		
	}
	public EagleHistoricalDataProvider(Instrument instrument,InstrumentStoreService instrumentStoreService,ExtractDataJobRepository extractDataJobRepository) {
		this.instrument = instrument;
		this.instrumentStoreService = instrumentStoreService;
		this.extractDataJobRepository = extractDataJobRepository;
		
	}

	@Override
	public void historicalData(Bar bar, boolean hasGap) {
		InstrumentHistoricalData  historicalData = buildInstrumentHistoricalData(this.instrument, bar,hasGap);
		extractDataJobRepository.updateStatus(this.instrument.getSymbol(), JobStatus.COMPLETED);
		LOGGER.debug(historicalData.toString());
		//Store Data in file
		instrumentStoreService.storeRawData(historicalData);
		
	}

	@Override
	public void historicalDataEnd() {
		LOGGER.debug("Received Historical Data End :"+this.instrument.getSymbol());
	}
	
	//-------Helpers---------
	private InstrumentHistoricalData buildInstrumentHistoricalData(Instrument instrument, Bar bar, boolean hasGap){
		InstrumentHistoricalData historicalData = new InstrumentHistoricalData();
		historicalData.setInstrument(instrument);
		historicalData.setClose(bar.close());
		historicalData.setCount(bar.count());
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		historicalData.setDate(dateFormat.format(new Date(bar.time()* 1000)));
		historicalData.setHigh(bar.high());
		historicalData.setLow(bar.low());
		historicalData.setOpen(bar.open());
		historicalData.setVolume(bar.volume());
		historicalData.setAdjClose(bar.close());
		historicalData.setWap(bar.wap());
		historicalData.setHasGaps(hasGap);
		return historicalData;
	}
}
