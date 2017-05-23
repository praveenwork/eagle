package com.eagle.workflow.engine.tws.api;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;
import com.eagle.workflow.engine.tws.data.providers.EagleTWSConnectionProvider;
import com.eagle.workflow.engine.tws.model.EagleAPIConnection;
import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController;

public class EagleAPI extends ApiController{

	private Logger LOGGER = LoggerFactory.getLogger(EagleAPI.class);
	
	@Autowired
	private EagleTWSConnectionProvider connectionProvider;

	private EagleAPIConnection eagleAPIConnection;
	
	/**
	 * @param handler
	 * @param inLogger
	 * @param outLogger
	 * @param host
	 * @param port
	 * @param clientId
	 */
	public EagleAPI(EagleTWSConnectionProvider connectionProvider, ILogger inLogger, ILogger outLogger, String host, int port,
			int clientId) {
		super(connectionProvider, inLogger, outLogger);
		this.connectionProvider = connectionProvider;
		eagleAPIConnection = new EagleAPIConnection(host,port,clientId);
		LOGGER.debug("ApiController Initialized. Trying to connect TWS..");
		//this.connect(host,port,clientId);
	}

	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController#connect(java.lang.String, int, int)
	 */
	public void connect(String host, int port, int clientId){
		try{
			super.connect(host, port, clientId);
			if(isConnected()){
				LOGGER.debug("TWS Connected.");
			}else {
				LOGGER.error("TWS not Connected. Please make sure TWS is up and running.");
			}
		} catch (EagleException e) {
			LOGGER.error(e.getMessage(), e);
			throw  e;

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new EagleException(EagleError.FAILED_TO_CONNECT_TWS, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ib.controller.ApiController#disconnect()
	 */
	public void disconnect(){
		super.disconnect();
	}
	
	/**
	 * @return
	 */
	public boolean isConnected(){
		return this.connectionProvider.isConnected();
	}
	
	/**
	 * 
	 */
	public void checkAndConnect() throws EagleException{
		try {
			if(!isConnected()){
				LOGGER.debug("TWS connection lost. Trying to reconnect TWS.. ");
				this.connect(eagleAPIConnection.getHost(), eagleAPIConnection.getPort(), eagleAPIConnection.getClientId());
			}
		} catch (EagleException e) {
			throw e;
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_CONNECT_TWS, e.getMessage(),e);
		}
	}
	
	public ArrayList<String> getAccounts(){
		try {
			checkAndConnect();
			return this.connectionProvider.getAccountList();
		} catch (EagleException e) {
			throw e;
		} catch (Exception e) {
			throw new EagleException(EagleError.FAILED_TO_CONNECT_TWS, e.getMessage(),e);
		}
	}
}