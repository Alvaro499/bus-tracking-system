package com.bustracking.shared.exception;

public class BusinessRuleException extends ApplicationException {
    public BusinessRuleException(ErrorCode errorCode, String userMessage, String devMessage) {
        super(errorCode, userMessage, devMessage);
    }

    public BusinessRuleException(ErrorCode errorCode, String generalMessage) {
        super(errorCode, generalMessage, generalMessage);
    }
}
