package com.eagle.workflow.engine.utils;

import java.io.Serializable;

/**
 * @author ppasupuleti
 *
 */
public class EagleProcessExecutorResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean execStatus;
	private int errorCode;
	private String response;
	private String errorMessage;

	public boolean isExecStatus() {
		return execStatus;
	}

	public void setExecStatus(boolean execStatus) {
		this.execStatus = execStatus;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
