package com.eagle.workflow.engine.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.eagle.contract.model.CFDInstrument;
import com.eagle.contract.model.ComboInstrument;
import com.eagle.contract.model.ForexInstrument;
import com.eagle.contract.model.FuturesInstrument;
import com.eagle.contract.model.IndexInstrument;
import com.eagle.contract.model.Instrument;
import com.eagle.contract.model.InstrumentType;
import com.eagle.contract.model.OptionsInstrument;
import com.eagle.contract.model.StockInstrument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ppasupuleti
 *
 */
public class InstrumentRepositoryFactory {

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * @param instrumentInputStream
	 * @return
	 */
	public InstrumentRepository createInstrumentRepository(InputStream instrumentInputStream){
		List<Map<String, Object>> dataMap;
		try {
            dataMap = objectMapper.readValue(instrumentInputStream, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
		List<Instrument> instrumentLists  = new ArrayList<>();
		Instrument instrument = null;
		for (Map<String, Object> row : dataMap) {
			instrument = getInstrumentObject((String) row.get("type"));
			instrument.setName((String) row.get("name"));
			instrument.setCurrency((String) row.get("currency"));
			instrument.setExchange((String) row.get("exchnage"));
			instrument.setPrimaryExchange((String) row.get("primaryExchange"));
			instrument.setSymbol((String) row.get("symbol"));
			instrumentLists.add(instrument);
			instrument = null;
		}
		return new InstrumentRepository(instrumentLists);
	}
	
	//----------Helpers------------
	private Instrument getInstrumentObject(String type){
		Instrument instrument = null;
		if(InstrumentType.STOCK.name().equals(type)){
			instrument = new StockInstrument();
		} else if(InstrumentType.CFD.name().equals(type)){
			instrument = new CFDInstrument();
		} else if(InstrumentType.COMBO.name().equals(type)){
			instrument = new ComboInstrument();
		} else if(InstrumentType.FOREX.name().equals(type)){
			instrument = new ForexInstrument();
		} else if(InstrumentType.FUTURES.name().equals(type)){
			instrument = new FuturesInstrument();
		} else if(InstrumentType.INDEX.name().equals(type)){
			instrument = new IndexInstrument();
		} else if(InstrumentType.OPTION.name().equals(type)){
			instrument = new OptionsInstrument();
		}
		return instrument;
	}
}
