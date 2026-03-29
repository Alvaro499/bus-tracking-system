package com.bustracking.admin.infrastructure.web.error;


// ErrorResponse is not generic. It's specific about how we expose errors (HTTP/gRPC/Kafka). 
public class ErrorResponse {
    
    private String message;
    private String code;
    private long timestamp;

    public ErrorResponse(String code, String message) {

        this.message = message;
        this.code = code;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
