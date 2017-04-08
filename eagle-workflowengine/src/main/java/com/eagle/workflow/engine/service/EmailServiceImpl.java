package com.eagle.workflow.engine.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eagle.boot.config.exception.EagleError;
import com.eagle.boot.config.exception.EagleException;
import com.eagle.contract.model.EmailRequest;
import com.eagle.workflow.engine.config.EagleEmailProperties;

/**
 * @author ppasupuleti
 *
 */
@Service
public class EmailServiceImpl implements EmailService{

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
	
	@Autowired
	private EagleEmailProperties emailProperties;
	
	public EmailServiceImpl(EagleEmailProperties emailProperties) {
		this.emailProperties = emailProperties;
	}

	@Override
	public void send(EmailRequest emailRequest) throws EagleException {
		try {
			
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", emailProperties.getSmtpHost());
			props.put("mail.smtp.port", emailProperties.getSmtpPort());
			
			Session session = Session.getInstance(props,  new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(emailProperties.getFromEmail(), emailProperties.getPassword());
				}
			  }); // no authentication
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailProperties.getFromEmail()));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(emailProperties.getToEmail()));
			message.setSubject(emailRequest.getEmailSubject());
			message.setText(emailRequest.getEmailContent());

			Transport.send(message);

			LOGGER.debug("Email dispatched.");
			
		} catch (Exception e) {
			LOGGER.error("Failed sending eamil.",e);
			throw new EagleException(EagleError.FAILED_TO_SEND_MAIL, e);
		}
	}

}
