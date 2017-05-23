package com.eagle.workflow.engine.tws.data.providers;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;
import com.ib.controller.ApiController.IConnectionHandler;

@Component
public class EagleTWSConnectionProvider implements IConnectionHandler {
	private Logger LOGGER = LoggerFactory.getLogger(EagleTWSConnectionProvider.class);
	
	private static boolean connected;
	
	private ArrayList<String> mAccountList = null;
	
	public EagleTWSConnectionProvider(){
		LOGGER.info("in EagleTWSConnectionProvider");
	}
	
	public boolean isConnected() {
		return connected;
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.IConnectionHandler#accountList(java.util.ArrayList)
	 */
	@Override
	public void accountList(ArrayList<String> accountList) {
		mAccountList = accountList;
	}

	public ArrayList<String> getAccountList() {
		return mAccountList;
	}
	
	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.IConnectionHandler#connected()
	 */
	@Override
	public void connected() {
		LOGGER.info("in Connected");
		connected = true;

	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.IConnectionHandler#disconnected()
	 */
	@Override
	public void disconnected() {
		LOGGER.info("in disconnected");
		connected = false;

	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.IConnectionHandler#error(java.lang.Exception)
	 */
	@Override
	public void error(Exception exception) {
		LOGGER.error("======in Error",exception);
		throw new EagleException(EagleError.FAILED_TO_CONNECT_TWS, exception);

	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.IConnectionHandler#message(int, int, java.lang.String)
	 */
	@Override
	public void message(int id, int messageCode, String message) {
		LOGGER.info(id + " - "  + messageCode + " - " +message);
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController.IConnectionHandler#show(java.lang.String)
	 */
	@Override
	public void show(String arg0) {
		LOGGER.info("in show"+arg0);
	}
}
