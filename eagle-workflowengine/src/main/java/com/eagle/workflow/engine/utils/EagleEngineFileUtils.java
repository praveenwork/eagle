package com.eagle.workflow.engine.utils;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eagle.workflow.engine.config.EagleWorkFlowEngineProperties;

/**
 * @author ppasupuleti
 *
 */
@Component
public class EagleEngineFileUtils {
	
	@Autowired 
	private EagleWorkFlowEngineProperties engineProperties;
	
	public String eagleHomeDirectoryPath(){
		return engineProperties.getEagleHomeDirectory();
	}
	
	public String getRawDataPath(){
		StringBuilder rawDataPath = new StringBuilder(eagleHomeDirectoryPath());
		rawDataPath.append(File.separator);
		rawDataPath.append(engineProperties.getRawDataDirectory());
		rawDataPath.append(File.separator);
		return rawDataPath.toString();
	}
	
	public String getToolDataPath(){
		StringBuilder toolsDataPath = new StringBuilder(eagleHomeDirectoryPath());
		toolsDataPath.append(File.separator);
		toolsDataPath.append(engineProperties.getToolsDirectory());
		toolsDataPath.append(File.separator);
		return toolsDataPath.toString();
	}
	
	public String getModelDataPath(){
		StringBuilder modelDataPath = new StringBuilder(eagleHomeDirectoryPath());
		modelDataPath.append(File.separator);
		modelDataPath.append(engineProperties.getModelDataDirectory());
		modelDataPath.append(File.separator);
		return modelDataPath.toString();
	}
	
	public String getModelPKLPath(){
		StringBuilder pklPath = new StringBuilder(getModelDataPath());
		pklPath.append("pkl");
		pklPath.append(File.separator);
		return pklPath.toString();
	}
	
	public String getModelOutputPath(){
		StringBuilder modelDataPath = new StringBuilder(getModelDataPath());
		modelDataPath.append("output");
		modelDataPath.append(File.separator);
		return modelDataPath.toString();
	}
	
	public String getModelDataToolsPath(){
		StringBuilder modelDataPath = new StringBuilder(getToolDataPath());
		modelDataPath.append("applyModel");
		modelDataPath.append(File.separator);
		return modelDataPath.toString();
	}
	
	
	
	public String getModelDataToolCodePath(){
		StringBuilder modelCodePath = new StringBuilder(getModelDataToolsPath());
		modelCodePath.append("Code");
		modelCodePath.append(File.separator);
		return modelCodePath.toString();
	}
	
	
	
	
	public String getEnrichDataPath(){
		StringBuilder enrichDataPath = new StringBuilder(eagleHomeDirectoryPath());
		enrichDataPath.append(File.separator);
		enrichDataPath.append(engineProperties.getEnrichDataDirectory());
		enrichDataPath.append(File.separator);
		return enrichDataPath.toString();
	}
	
	public String getEnrichModelPath(){
		StringBuilder enrichDataPath = new StringBuilder(getEnrichDataPath());
		enrichDataPath.append("enrichModel");
		enrichDataPath.append(File.separator);
		return enrichDataPath.toString();
	}
}
