package com.bustracking.shared.exception;

public class ValidationException extends ApplicationException {
    
    public ValidationException(ErrorCode errorCode, String userMessage, String devMessage) {
        super(errorCode, userMessage, devMessage);
    }
}
