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
@ConfigurationProperties(prefix= "eagle-workflow.enrichdata")
public class EagleEnrichDataProperties {
	private static final Logger LOGGER = LoggerFactory.getLogger(EagleEnrichDataProperties.class);
	
	@NotBlank(message = "No Eagle workflow enrichApp property was provided ")
	private String enrichApp;

	public String getEnrichApp() {
		return enrichApp;
	}

	public void setEnrichApp(String enrichApp) {
		this.enrichApp = enrichApp;
	}
}
