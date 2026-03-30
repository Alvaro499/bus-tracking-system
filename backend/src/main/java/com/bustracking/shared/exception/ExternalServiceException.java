package com.bustracking.shared.exception;

// It goes in shared because it's a generic exception that can be used across different modules. It represents an error that occurs when calling an external service (like a third-party API).
public class ExternalServiceException extends ApplicationException {
    public ExternalServiceException(String userMessage, String devMessage, Throwable cause) {
        super(ErrorCode.EXTERNAL_ERROR, userMessage, devMessage, cause);
    }

    // If we only have the technical message (from the external system)
    public ExternalServiceException(String devMessage, Throwable cause) {
        super(ErrorCode.EXTERNAL_ERROR, "Error al comunicarse con un servicio externo", devMessage, cause);
    }
}
    

/**
 * Example for future classes:

try {
    externalApi.call();
} catch (ExternalException e) {
    throw new ApplicationException(ErrorCode.EXTERNAL_ERROR, "Fallo externo", e);
}


 */