package com.eagle.workflow.engine.config;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix= "eagle-workflow.IBGateway")
public class EagleIBGatewayProperties {
	private static final Logger LOGGER = LoggerFactory.getLogger(EagleIBGatewayProperties.class);
	
	@NotBlank(message = "No IBGateway host property was provided")
	private String host;
	
	@NotNull(message = "No IBGateway port property was provided")
	private Integer port;
	
	@NotNull(message = "No IBGateway clientId property was provided")
	private Integer clientId;
	
	@NotBlank(message = "No IBGateway userName property was provided")
	private String userName;
	
	@NotBlank(message = "No IBGateway password property was provided")
	private String password;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getClientId() {
		return clientId;
	}
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}
	@Override
	public String toString() {
		return "IBGatewayProperties{host'" + host + "',port" + port + "',clientId" + clientId + ",userName'" + userName + "',password'" + password
				+ "'}";
	}
	
}
