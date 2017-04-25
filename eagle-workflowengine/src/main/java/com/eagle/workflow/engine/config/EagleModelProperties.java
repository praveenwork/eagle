package com.eagle.workflow.engine.config;

import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author ppasupuleti
 *
 */

@Configuration
@ConfigurationProperties(prefix= "eagle-workflow.model")
public class EagleModelProperties {
	private static final Logger LOGGER = LoggerFactory.getLogger(EagleModelProperties.class);
	
	@NotBlank(message = "No Eagle workflow modelAppName property was provided ")
	private String modelAppName;
	
	@NotBlank(message = "No Eagle workflow pklFilePath property was provided ")
	private String pklFilePath;

	public String getModelAppName() {
		return modelAppName;
	}

	public void setModelAppName(String modelAppName) {
		this.modelAppName = modelAppName;
	}

	public String getPklFilePath() {
		return pklFilePath;
	}

	public void setPklFilePath(String pklFilePath) {
		this.pklFilePath = pklFilePath;
	}
}