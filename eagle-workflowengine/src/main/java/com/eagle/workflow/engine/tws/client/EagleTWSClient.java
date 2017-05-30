package com.eagle.workflow.engine.tws.client;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.model.Instrument;
import com.eagle.contract.service.BrokerService;
import com.eagle.workflow.engine.repository.ExtractDataJobRepository;
import com.eagle.workflow.engine.repository.PositionDataJobRepository;
import com.eagle.workflow.engine.store.InstrumentStoreService;
import com.eagle.workflow.engine.tws.api.EagleAPI;
import com.eagle.workflow.engine.tws.data.providers.EagleAccountDataProvider;
import com.eagle.workflow.engine.tws.data.providers.EagleHistoricalDataProvider;
import com.eagle.workflow.engine.tws.util.NewContractFactory;
import com.ib.controller.NewContract;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.ib.controller.Types.WhatToShow;

@Component
public class EagleTWSClient  implements BrokerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EagleTWSClient.class);
	
	@Autowired 	
	private EagleAPI eagleAPI;
	
	@Autowired
	private ExtractDataJobRepository extractDataJobRepository;
	
	@Autowired
	private PositionDataJobRepository positionDataJobRepository;
	
	@Autowired
	private InstrumentStoreService instrumentStoreService;
	
	public EagleTWSClient() {
	}
	
	//----------- Client calls--------
	@Override
	public void extractHistoricalData(Instrument instrument, int duration) throws EagleException {
		try {
			LOGGER.debug("Requsting Historcial Data for Instrument [From IB]: "+instrument.getSymbol());
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
			String endDateTime = LocalDateTime.now().format(formatter);
			
			// build new Contract
			NewContract contract = NewContractFactory.getNewContract(instrument);
			
			//Check the IB connection
			eagleAPI.checkAndConnect();

			// Get the historical Data for Instrument.
			EagleHistoricalDataProvider provider = new EagleHistoricalDataProvider(instrument,instrumentStoreService,extractDataJobRepository);
			
			eagleAPI.reqHistoricalData(contract, endDateTime, duration, DurationUnit.DAY,  BarSize._1_day, WhatToShow.TRADES, false, provider);
		} catch (EagleException e) {
			throw e;
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_EXTRACT_DATA, e.getMessage(), e);
		}
	}
	
	@Override
	public List<String> getAccounts() {
		try {
			LOGGER.debug("Requsting Getting Accounts: ");
			//Check the IB connection
			eagleAPI.checkAndConnect();
			return eagleAPI.getAccounts();
		} catch (EagleException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EagleException(EagleError.FAILED_TO_GET_ACCOUNTS,"", e);
		}	
	}

	@Override
	public void getPortifolioPosition(Instrument instrument, String accountCode) {
		try {
			LOGGER.debug("Requsting Portifolio Positionfor Instrument: "+instrument.getSymbol());
			
			// build new Contract
			NewContract contract = NewContractFactory.getNewContract(instrument);
			EagleAccountDataProvider accountDataProvider = new EagleAccountDataProvider(instrument,positionDataJobRepository);
			eagleAPI.reqAccountUpdates(true, accountCode, accountDataProvider);
			LOGGER.debug("Requested to IB");		
		} catch (EagleException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EagleException(EagleError.FAILED_TO_GET_INSTRUMENT_POSITION, instrument.getSymbol(), e);
		}
	}
}
