package com.eagle.workflow.engine.tws.data.providers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.eagle.contract.model.Instrument;
import com.eagle.contract.model.InstrumentPosition;
import com.eagle.workflow.engine.repository.JobStatus;
import com.eagle.workflow.engine.repository.PositionDataJobRepository;
import com.eagle.workflow.engine.tws.util.NewContractFactory;
import com.ib.controller.ApiController.IPositionHandler;
import com.ib.controller.NewContract;

/**
 * @author ppasupuleti
 *
 */
public class EaglePositionDataProvider implements IPositionHandler {

	private static ConcurrentMap<String, InstrumentPosition> positionMap  = new ConcurrentHashMap<>();
	
	private PositionDataJobRepository positionDataJobRepository;
	
	private Instrument instrument;
	
	public EaglePositionDataProvider(Instrument instrument,PositionDataJobRepository positionDataJobRepository) {
		this.instrument = instrument;
		this.positionDataJobRepository = positionDataJobRepository;
		InstrumentPosition position = new InstrumentPosition();
		position.setInstrument(this.instrument);
		position.setPosition(0);
		positionMap.put(this.instrument.getSymbol(), position);
	}
	
	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.IPositionHandler#position(java.lang.String, com.ib.controller.NewContract, int, double)
	 */
	@Override
	public void position( String account, NewContract contract, int position, double avgCost) {
		String symbol = contract.symbol();
		InstrumentPosition instrumentPosition = new InstrumentPosition();
		instrumentPosition.setAccount(account);
		instrumentPosition.setAverageCost(avgCost);
		instrumentPosition.setInstrument(NewContractFactory.getInstrument(contract));
		instrumentPosition.setPosition(position);
		positionMap.put(symbol, instrumentPosition);
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.IPositionHandler#positionEnd()
	 */
	@Override
	public void positionEnd() {
		positionDataJobRepository.updateStatus(this.instrument.getSymbol(), JobStatus.COMPLETED);
	}
	
	public static InstrumentPosition getInstrumentPositionData(String instrumentSymbol) {
		return positionMap.get(instrumentSymbol);
	}
}
