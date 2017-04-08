package com.eagle.workflow.engine.tws.model;

import java.io.Serializable;

public class EagleAPIConnection implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String host;
	
	private Integer port;
	
	private Integer clientId;

	public EagleAPIConnection(String host, Integer port, Integer clientId) {
		this.host = host;
		this.port = port;
		this.clientId = clientId;
	}

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

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}
}
