package com.eagle.workflow.engine.config;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix= "eagle-workflow")
public class EagleWorkFlowEngineProperties {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EagleWorkFlowEngineProperties.class);
	
	@NotBlank(message = "No Eagle workflow Job schedule property was provided ")
	private String schedule;
	
	@NotBlank(message = "No Eagle workflow eagleHomeDirectory property was provided ")
	private String eagleHomeDirectory;
	
	@NotBlank(message = "No Eagle workflow toolsDirectory property was provided ")
	private String toolsDirectory;
	
	@NotBlank(message = "No Eagle workflow rawDataDirectory property was provided ")
	private String rawDataDirectory;
	
	@NotBlank(message = "No Eagle workflow rawDataFileType property was provided ")
	private String rawDataFileType;
	
	@NotBlank(message = "No Eagle workflow enrichDataDirectory property was provided ")
	private String enrichDataDirectory;
	
	@NotBlank(message = "No Eagle workflow enrichDataModelDirectory property was provided ")
	private String enrichDataModelDirectory;
	
	@NotBlank(message = "No Eagle workflow modelDataDirectory property was provided ")
	private String modelDataDirectory;
	
	@NotNull(message = "No Eagle workflow instrument properties were provided ")
	private InstrumentProperties instrument;
	
	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public InstrumentProperties getInstrument() {
		return instrument;
	}

	public void setInstrument(InstrumentProperties instrument) {
		this.instrument = instrument;
	}

	public String getEagleHomeDirectory() {
		return eagleHomeDirectory;
	}

	public void setEagleHomeDirectory(String eagleHomeDirectory) {
		this.eagleHomeDirectory = eagleHomeDirectory;
	}

	public String getToolsDirectory() {
		return toolsDirectory;
	}

	public void setToolsDirectory(String toolsDirectory) {
		this.toolsDirectory = toolsDirectory;
	}

	public String getRawDataDirectory() {
		return rawDataDirectory;
	}

	public void setRawDataDirectory(String rawDataDirectory) {
		this.rawDataDirectory = rawDataDirectory;
	}

	public String getRawDataFileType() {
		return rawDataFileType;
	}

	public void setRawDataFileType(String rawDataFileType) {
		this.rawDataFileType = rawDataFileType;
	}

	public String getEnrichDataDirectory() {
		return enrichDataDirectory;
	}

	public void setEnrichDataDirectory(String enrichDataDirectory) {
		this.enrichDataDirectory = enrichDataDirectory;
	}

	public String getModelDataDirectory() {
		return modelDataDirectory;
	}

	public void setModelDataDirectory(String modelDataDirectory) {
		this.modelDataDirectory = modelDataDirectory;
	}

	public String getEnrichDataModelDirectory() {
		return enrichDataModelDirectory;
	}

	public void setEnrichDataModelDirectory(String enrichDataModelDirectory) {
		this.enrichDataModelDirectory = enrichDataModelDirectory;
	}
}
