package com.eagle.workflow.engine.store;

import java.util.List;
import java.util.Map;

/**
 * @author ppasupuleti
 *
 */
public interface EagleEngineDataProcessor<T> {
	
	public List<T> readData(String path);
	
	public boolean writeData(String path, T data);
	
	public T getLastRecord(Class<T> theClass, String path);
	
	public T getLastRecord(Class<T> theClass, String path, boolean customSelection);
	
}
