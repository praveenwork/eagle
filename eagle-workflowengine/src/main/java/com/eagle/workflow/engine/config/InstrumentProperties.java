package com.eagle.workflow.engine.config;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "eagle-workflow.instrument")
public class InstrumentProperties {
	private static final Logger LOGGER = LoggerFactory.getLogger(InstrumentProperties.class);
	
	@NotNull(message = "No Eagle workflow instrument selected property was provided ")
	private Boolean selected;
	
	@NotBlank(message = "No Eagle workflow instrument inputFilePath property was provided ")
	private String inputFilePath;
	
	@NotBlank(message = "No Eagle workflow instrument store Data Directory property was provided ")
	private String storeDataDirectory;
	
	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public String getStoreDataDirectory() {
		return storeDataDirectory;
	}

	public void setStoreDataDirectory(String storeDataDirectory) {
		this.storeDataDirectory = storeDataDirectory;
	}
}
