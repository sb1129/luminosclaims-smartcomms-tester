package com.gb.smartcomms.service;

public class SmartCommsServiceException extends RuntimeException {
    public SmartCommsServiceException(final String message) {
        super(message);
    }

    public SmartCommsServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
