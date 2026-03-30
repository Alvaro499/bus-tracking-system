package com.bustracking.shared.exception;

public class NotFoundException extends ApplicationException {
    public NotFoundException(ErrorCode errorCode, String userMessage, String devMessage) {
        super(errorCode, userMessage, devMessage);
    }

    // Convenience constructor cuando el mensaje es el mismo
    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message, message);
    }
}