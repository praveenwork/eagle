package com.eagle.workflow.engine.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eagle.contract.model.Instrument;

/**
 * @author ppasupuleti
 *
 */
public class InstrumentRepository {

	private Map<String, Instrument> instrumentRepository = new HashMap<String, Instrument>();
	
	public InstrumentRepository(List<Instrument> instruments) {
		for (Instrument instrument : instruments) {
			instrumentRepository.put(instrument.getSymbol(), instrument);
		}
	}
	 
	public List<Instrument> getInstruments(){
		List<Instrument> instrumentsList = new ArrayList<>(instrumentRepository.size());
		for (Map.Entry<String, Instrument> entry : instrumentRepository.entrySet()) {
			instrumentsList.add(entry.getValue());
		}
		return instrumentsList;
	}
	
	public Instrument getInstrument(String symbol){
		return instrumentRepository.get(symbol);
	}
}
