package com.eagle.boot.config.exception;

import org.springframework.http.HttpStatus;
public class EagleError {

    public static final EagleError INTERNAL_SERVER_ERROR =
            new EagleError("'%s' Intenal Server Error.", HttpStatus.BAD_REQUEST.value());
    
    public static final EagleError FAILED_TO_EXTRACT_DATA =
            new EagleError("'%s' Failed to extract Data .", HttpStatus.BAD_REQUEST.value());
    
    public static final EagleError FAILED_TO_CONNECT_TWS =
            new EagleError("'%s' failed to connect TWS server.", HttpStatus.BAD_REQUEST.value());
    
    public static final EagleError FAILED_TO_SEND_MAIL =
            new EagleError("'%s' failed to send email.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    
    
    private final String messageFormat;
    private final int httpResponseCode;

    public EagleError(String messageFormat, int httpResponseCode) {
        this.messageFormat = messageFormat;
        this.httpResponseCode = httpResponseCode;
    }

    public String getMessageFormat() {
        return messageFormat;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }
}
