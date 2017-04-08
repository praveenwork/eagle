package com.eagle.workflow.engine.service;

import com.eagle.boot.config.exception.EagleException;

/**
 * @author ppasupuleti
 *
 */
public interface DataExtractorService {
	
	public boolean extractData() throws EagleException;

}
