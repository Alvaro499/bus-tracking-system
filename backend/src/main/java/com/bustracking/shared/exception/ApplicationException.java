package com.bustracking.shared.exception;

public abstract class ApplicationException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String userMessage;
    private final String devMessage;

    protected ApplicationException(
        ErrorCode errorCode,
        String userMessage,
        String devMessage
        
    ) {
        super(devMessage);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.devMessage = devMessage;
    }

    protected ApplicationException(
            ErrorCode errorCode,
            String userMessage,
            String devMessage,
            Throwable cause
        ) {
            super(devMessage, cause);
            this.errorCode = errorCode;
            this.userMessage = userMessage;
            this.devMessage = devMessage;
    }

    // It gets the technical message from Throwable
    @Override
    public String getMessage() {
        return this.devMessage;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }

    public String getUserMessage(){
        return this.userMessage;
    }

    public String getDevMessage() {
        return this.devMessage;
    }
    
}


