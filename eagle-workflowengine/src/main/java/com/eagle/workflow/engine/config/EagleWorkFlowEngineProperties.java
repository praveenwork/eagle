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
	
	@NotBlank(message = "No Eagle workflow Job property was provided ")
	private String schedule;
	
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
}
