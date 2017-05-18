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
    
    public static final EagleError FAILED_TO_EXECUTE_PROCESS =
            new EagleError("Failed execute the command: ''%s''.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    
    
    public static final EagleError INVALID_PATH =
            new EagleError("Invalid file Path: ''%s''.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    
    public static final EagleError FAILD_TO_WRITE_DATA_INFILE =
            new EagleError("Failed to write data in file : ''%s''.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    
    public static final EagleError FAILD_TO_READ_THE_LAST_RECORD =
            new EagleError("Failed to read the last record from the file : ''%s''.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    
    public static final EagleError EMPTY_OBJECT =
            new EagleError("No records found to write in file : ''%s''.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    
    public static final EagleError FAILED_TO_EXECUTE_ENRICHDATA_STEP =
            new EagleError("EnrichData step failed.''%s''.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    
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
