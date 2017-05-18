package com.eagle.workflow.engine.store;

import java.util.List;

/**
 * @author ppasupuleti
 *
 */
public interface EagleEngineDataProcessor<T> {
	
	public List<T> readData(String path);
	
	public boolean writeData(String path, T data);
	
	public T getLastRecord(Class<T> theClass, String path);
	
}
