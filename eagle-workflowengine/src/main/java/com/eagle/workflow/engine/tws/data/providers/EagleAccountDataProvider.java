package com.eagle.workflow.engine.tws.data.providers;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eagle.contract.model.AccountPosition;
import com.eagle.contract.model.Instrument;
import com.eagle.workflow.engine.repository.JobStatus;
import com.eagle.workflow.engine.repository.PositionDataJobRepository;
import com.ib.controller.ApiController.IAccountHandler;
import com.ib.controller.NewContract;
import com.ib.controller.Position;

/**
 * @author ppasupuleti
 *
 */
public class EagleAccountDataProvider  implements IAccountHandler{

	private static final Logger LOGGER = LoggerFactory.getLogger(EagleAccountDataProvider.class);
	
	private Instrument instrument;
	private AccountPosition accountPosition;
	private static ConcurrentHashMap<String, AccountPosition> instrumentPositionMap = new ConcurrentHashMap<>();
	private PositionDataJobRepository positionDataJobRepository;
	
	public EagleAccountDataProvider(Instrument instrument,PositionDataJobRepository positionDataJobRepository) {
		this.instrument = instrument;
		accountPosition = new AccountPosition();
		this.positionDataJobRepository = positionDataJobRepository;
		instrumentPositionMap.put(this.instrument.getSymbol(), accountPosition);
	}

	public AccountPosition getAccountPosition() {
		return accountPosition;
	}

	@Override
	public void accountDownloadEnd(String accountName) {
		accountPosition.setAccountName(accountName);
	}

	@Override
	public void accountTime(String timeStamp) {
		accountPosition.setTimeStamp(timeStamp);
	}

	@Override
	public void accountValue(String key, String value, String currency, String accountName) {
		//LOGGER.info("accountValue : [key :"+ key + ", value: "+ value+", currency: "+currency+ ", accountName" + accountName +"]");
	}

	@Override
	public void updatePortfolio(Position position) {
		accountPosition.setAccountName(position.account());
		accountPosition.setPosition(position.position());
		accountPosition.setMarketPrice(position.marketPrice());
		accountPosition.setMarketValue(position.marketValue());
		accountPosition.setContractDescription(position.contract().description());
		instrumentPositionMap.put(position.contract().symbol(), accountPosition);
		//if(position.contract().symbol().equals(this.instrument.getSymbol())){
			positionDataJobRepository.updateStatus(this.instrument.getSymbol(), JobStatus.COMPLETED);
		//}
	}
	
	public static AccountPosition getAcccountPositionData(String instrumentSymbol) {
		return instrumentPositionMap.get(instrumentSymbol);
	}
	
	
}
