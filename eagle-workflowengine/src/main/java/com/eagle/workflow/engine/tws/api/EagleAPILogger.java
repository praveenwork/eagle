package com.eagle.workflow.engine.tws.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;
import com.ib.controller.ApiConnection.ILogger;

@Component
public class EagleAPILogger implements ILogger {

	private static Logger LOGGER = LoggerFactory.getLogger(EagleAPILogger.class);
	
	/* (non-Javadoc)
	 * @see com.ib.controller.ApiConnection.ILogger#log(java.lang.String)
	 */
	@Override
	public void log(String logMessage) {
		//throw new EagleException(EagleError.INTERNAL_SERVER_ERROR,logMessage);
	}
}
