package com.eagle.workflow.engine.tws.data.providers;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eagle.contract.model.Instrument;
import com.eagle.contract.model.InstrumentRealTimeData;
import com.eagle.workflow.engine.repository.JobStatus;
import com.eagle.workflow.engine.repository.RealTimeDataJobRepository;
import com.ib.controller.ApiController.ITopMktDataHandler;
import com.ib.controller.NewTickType;
import com.ib.controller.Types.MktDataType;

/**
 * @author ppasupuleti
 *
 */
@Component
public class EagleRealTimeMarketDataProvider implements ITopMktDataHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(EagleRealTimeMarketDataProvider.class);

	private Instrument instrument;

	private RealTimeDataJobRepository realTimeDataJobRepository;
    
	private InstrumentRealTimeData instrumentRealTimeData;
	
	private static ConcurrentHashMap<String,InstrumentRealTimeData> realTimeDataMap = new ConcurrentHashMap<>();
	
	public EagleRealTimeMarketDataProvider(Instrument instrument, RealTimeDataJobRepository realTimeDataJobRepository){
		this.instrument = instrument;
		this.realTimeDataJobRepository = realTimeDataJobRepository;
		this.instrumentRealTimeData  = new InstrumentRealTimeData();
		realTimeDataMap.put(instrument.getSymbol(), this.instrumentRealTimeData);
	} 
	
	public EagleRealTimeMarketDataProvider(Instrument instrument) {
		this.instrument = instrument;
	}

	public EagleRealTimeMarketDataProvider() {
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.ITopMktDataHandler#marketDataType(com.ib.controller.Types.MktDataType)
	 */
	@Override
	public void marketDataType(MktDataType marketDataType) {
		if(marketDataType == marketDataType.Frozen){
			instrumentRealTimeData.setFrozen(true);
		} else {
			instrumentRealTimeData.setFrozen(false);
		}
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.ITopMktDataHandler#tickPrice(com.ib.controller.NewTickType, double, int)
	 */
	@Override
	public void tickPrice(NewTickType tickType, double price, int canAutoExecute) {
		System.out.println("Tick Price ["+instrument.getSymbol()+"] "+tickType.name() + " -- Price :"+price);
		addMarketDataPrice(tickType,price);
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.ITopMktDataHandler#tickSize(com.ib.controller.NewTickType, int)
	 */
	@Override
	public void tickSize(NewTickType tickType, int size) {
		System.out.println("Tick Size ["+instrument.getSymbol()+"] "+tickType.name() + " -- Size :"+size);
		addMarketDataSize(tickType,size);
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.ITopMktDataHandler#tickSnapshotEnd()
	 */
	@Override
	public void tickSnapshotEnd() {
		System.out.println("Tick Snapshot ended ["+instrument.getSymbol()+"]");
		realTimeDataJobRepository.updateStatus(this.instrument.getSymbol(), JobStatus.COMPLETED);
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.ITopMktDataHandler#tickString(com.ib.controller.NewTickType, java.lang.String)
	 */
	@Override
	public void tickString(NewTickType tickType, String value) {
		System.out.println("TICK STRING CALLED ["+instrument.getSymbol()+"] "+tickType.name() + "  "+value);
		addMarketDataTimeStamp(tickType,value);
	}
	
	public InstrumentRealTimeData getInstrumentRealTimeData(String instrumentSymbol){
		return realTimeDataMap.get(instrumentSymbol);
	}
	
	//--------Helpers----------
	private void addMarketDataPrice(NewTickType tickType, double price){
		if(tickType == NewTickType.BID){
			this.instrumentRealTimeData.setBid(price);
		} else if(tickType == NewTickType.ASK){
			this.instrumentRealTimeData.setAsk(price);
		} else if(tickType == NewTickType.LAST){
			this.instrumentRealTimeData.setLast(price);
		} else if(tickType == NewTickType.CLOSE){
			this.instrumentRealTimeData.setClose(price);
		}
	}
	
	private void addMarketDataSize(NewTickType tickType, Integer size){
		if(tickType == NewTickType.BID_SIZE){
			this.instrumentRealTimeData.setBidSize(size);
		} else if(tickType == NewTickType.ASK_SIZE){
			this.instrumentRealTimeData.setAskSize(size);
		} else if(tickType == NewTickType.VOLUME){
			this.instrumentRealTimeData.setVolume(size);
		}
	}
	
	private void addMarketDataTimeStamp(NewTickType tickType, String value){
		if(tickType == NewTickType.LAST_TIMESTAMP){
			this.instrumentRealTimeData.setLastTime(Long.parseLong( value) * 1000);
		}
	}
}
