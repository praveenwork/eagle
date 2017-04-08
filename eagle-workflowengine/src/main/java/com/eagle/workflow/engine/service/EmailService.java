package com.eagle.workflow.engine.service;

import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.model.EmailRequest;

/**
 * @author ppasupuleti
 *
 */
public interface EmailService {

	public void send(EmailRequest emailRequest) throws EagleException;
	
}
