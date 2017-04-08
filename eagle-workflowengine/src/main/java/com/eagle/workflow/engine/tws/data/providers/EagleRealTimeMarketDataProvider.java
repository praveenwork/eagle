package com.eagle.workflow.engine.tws.data.providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eagle.contract.model.Instrument;
import com.ib.controller.ApiController.ITopMktDataHandler;
import com.ib.controller.NewTickType;
import com.ib.controller.Types.MktDataType;

/**
 * @author ppasupuleti
 *
 */
public class EagleRealTimeMarketDataProvider implements ITopMktDataHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(EagleRealTimeMarketDataProvider.class);

	private Instrument instrument;
	
	public EagleRealTimeMarketDataProvider(){
		
	}
	
	public EagleRealTimeMarketDataProvider(Instrument instrument) {
		this.instrument = instrument;
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.ITopMktDataHandler#marketDataType(com.ib.controller.Types.MktDataType)
	 */
	@Override
	public void marketDataType(MktDataType marketDataType) {
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.ITopMktDataHandler#tickPrice(com.ib.controller.NewTickType, double, int)
	 */
	@Override
	public void tickPrice(NewTickType tickType, double price, int canAutoExecute) {
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.ITopMktDataHandler#tickSize(com.ib.controller.NewTickType, int)
	 */
	@Override
	public void tickSize(NewTickType tickType, int size) {
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.ITopMktDataHandler#tickSnapshotEnd()
	 */
	@Override
	public void tickSnapshotEnd() {
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.ITopMktDataHandler#tickString(com.ib.controller.NewTickType, java.lang.String)
	 */
	@Override
	public void tickString(NewTickType tickType, String value) {
	}
}
