package com.eagle.boot.config.exception;

public class EagleException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final EagleError eagleError;


    public EagleException(EagleError eagleError, Object... messageReplacementArgs) {
        this(eagleError, null, messageReplacementArgs);
    }

    public EagleException(EagleError eagleError, Throwable exception,
                                   Object... messageReplacementArgs) {
        super(String.format(eagleError.getMessageFormat(), messageReplacementArgs), exception);
        this.eagleError = eagleError;
    }

    public int getHttpResponseCode() {
        return eagleError.getHttpResponseCode();
    }
}
