package com.eagle.workflow.engine.config;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix= "eagle-workflow.email")
public class EagleEmailProperties {
	
	@NotBlank(message = "No Eagle workflow smtpHost property was provided ")
	private String smtpHost;
	
	@NotNull(message = "No Eagle workflow smtpPort properties were provided ")
	private Integer smtpPort;
	
	@NotBlank(message = "No Eagle workflow from Email property was provided ")
	private String fromEmail;
	
	@NotBlank(message = "No Eagle workflow password property was provided ")
	private String password;
	
	@NotBlank(message = "No Eagle workflow to Email property was provided ")
	private String toEmail;

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public Integer getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(Integer smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}
}