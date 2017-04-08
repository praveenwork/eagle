package com.eagle.workflow.engine.tws.data.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eagle.contract.model.Instrument;
import com.eagle.contract.model.InstrumentRealTimeBarData;
import com.ib.controller.ApiController.IRealTimeBarHandler;
import com.ib.controller.Bar;

/**
 * @author ppasupuleti
 *
 */
public class EagleIRealTimeBarsProvider implements IRealTimeBarHandler{
	private static final Logger LOGGER = LoggerFactory.getLogger(EagleIRealTimeBarsProvider.class);
	
	private Instrument instrument;
	
	public EagleIRealTimeBarsProvider(){
		
	}

	public EagleIRealTimeBarsProvider(Instrument instrument) {
		this.instrument = instrument;
	}

	@Override
	public void realtimeBar(Bar bar) {
		InstrumentRealTimeBarData realTimeBarData = buildRealTimeBarDaata(this.instrument,bar);
		LOGGER.debug(realTimeBarData.toString());
	}
	
	//-------Helpers-------
	private InstrumentRealTimeBarData buildRealTimeBarDaata(Instrument instrument, Bar bar){
		InstrumentRealTimeBarData realTimeBarData = new InstrumentRealTimeBarData();
		realTimeBarData.setInstrument(instrument);
		realTimeBarData.setClose(bar.close());
		realTimeBarData.setCount(bar.count());
		realTimeBarData.setDate(bar.formattedTime());
		realTimeBarData.setHigh(bar.high());
		realTimeBarData.setLow(bar.low());
		realTimeBarData.setOpen(bar.open());
		realTimeBarData.setVolume(bar.volume());
		realTimeBarData.setWap(bar.wap());
		return realTimeBarData;
	}
}
