package com.eagle.workflow.engine.yahoofinance.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.model.FuturesInstrument;
import com.eagle.contract.model.Instrument;
import com.eagle.contract.model.InstrumentHistoricalData;
import com.eagle.contract.service.BrokerHistoricalDataService;
import com.eagle.workflow.engine.store.InstrumentStoreService;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

/**
 * @author ppasupuleti
 *
 */
@Component
public class YahooFinanceClient implements BrokerHistoricalDataService {

	private static final Logger LOGGER = LoggerFactory.getLogger(YahooFinanceClient.class);

	@Autowired
	private InstrumentStoreService instrumentStoreService;

	/* (non-Javadoc)
	 * @see com.eagle.contract.service.BrokerService#extractHistoricalData(com.eagle.contract.model.Instrument, int)
	 */
	@Override
	public void extractHistoricalData(Instrument instrument, int duration) {
		try {
			LOGGER.debug("Requsting Historcial Data for Instrument [From Yahoo Finance]: "+instrument.getSymbol());

			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();
			from.add(Calendar.DATE, -duration);
			Stock stock = YahooFinance.get(instrument.getYfsymbol(), from, to, Interval.DAILY);
			List<InstrumentHistoricalData> historicalDataList = buildInstrumentHistoricalData(instrument,stock.getHistory());
			historicalDataList.add(includeCurrentDayData(instrument,stock.getQuote()));
			instrumentStoreService.storeRawData(buildInstrumentHistoricalData(instrument,stock.getHistory()));
			LOGGER.debug("Requsting Historcial Data Stored in file [From Yahoo Finance]: ");
		} catch (EagleException e) {
			throw e;
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_EXTRACT_DATA, e.getMessage(), e);
		}
	}
	
	//-----------Helpers---------
	private List<InstrumentHistoricalData> buildInstrumentHistoricalData(Instrument instrument, List<HistoricalQuote> historicalDataList){
		List<InstrumentHistoricalData> InstrumentHistoricalDataList = new ArrayList<InstrumentHistoricalData>(
				historicalDataList.size());
		InstrumentHistoricalData historicalData = null;
		for (HistoricalQuote historicalQuote : historicalDataList) {
			historicalData = new InstrumentHistoricalData();
			historicalData.setInstrument(instrument);
			historicalData.setClose(historicalQuote.getClose().doubleValue());
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			historicalData.setDate(dateFormat.format(historicalQuote.getDate().getTime()));
			historicalData.setHigh(historicalQuote.getHigh().doubleValue());
			historicalData.setLow(historicalQuote.getLow().doubleValue());
			historicalData.setOpen(historicalQuote.getOpen().doubleValue());
			historicalData.setVolume(historicalQuote.getVolume());
			historicalData.setAdjClose(historicalQuote.getAdjClose().doubleValue());
			InstrumentHistoricalDataList.add(historicalData);
			historicalData = null;
		}		
		return InstrumentHistoricalDataList;
	}
	
	private InstrumentHistoricalData includeCurrentDayData(Instrument instrument,  StockQuote stockQuote) throws ParseException{
		InstrumentHistoricalData currentData = new InstrumentHistoricalData();
		currentData.setInstrument(instrument);
		currentData.setClose(stockQuote.getPrice().doubleValue());
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		currentData.setDate(dateFormat.format(dateFormat.parse(stockQuote.getLastTradeDateStr())));
		currentData.setHigh(stockQuote.getDayHigh().doubleValue());
		currentData.setLow(stockQuote.getDayLow().doubleValue());
		currentData.setOpen(stockQuote.getOpen().doubleValue());
		currentData.setVolume(stockQuote.getVolume());
		currentData.setAdjClose(stockQuote.getPreviousClose().doubleValue());
		return currentData;
	}
	
}
