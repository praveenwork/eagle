package com.eagle.contract.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ppasupuleti
 *
 */
public class EmailRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String emailSubject;
	
	private String emailContent;
	
	private Map<String, String> emailTemplatesVals;
	
	public String getEmailSubject() {
		return emailSubject;
	}
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	public String getEmailContent() {
		return emailContent;
	}
	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}
	public Map<String, String> getEmailTemplatesVals() {
		return emailTemplatesVals;
	}
	public void setEmailTemplatesVals(Map<String, String> emailTemplatesVals) {
		this.emailTemplatesVals = emailTemplatesVals;
	}
}
