package com.bustracking.shared.infrastructure.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.bustracking.shared.exception.ApplicationException;

import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles invalid path variable format (UUID in wrong format).
     * Occurs when Spring cannot convert the path parameter to its expected type.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch error: Invalid {} parameter - {}", ex.getName(), ex.getValue());

        ErrorResponse response = new ErrorResponse(
            "INVALID_FORMAT",
            "Invalid parameter format"
        );
        // 400 = HttpStatusCode.BAD_REQUEST
        return ResponseEntity.status(400).body(response);
    }

    /**
     * Handles malformed JSON or missing required fields in request body.
     * Occurs when JSON cannot be deserialized to the expected object.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Invalid JSON: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
            "INVALID_INPUT",
            "Invalid JSON format or missing required fields"
        );
        // 400 = HttpStatusCode.BAD_REQUEST
        return ResponseEntity.status(400).body(response);
    }

    /**
     * Handles unsupported media types (missing or wrong Content-Type header).
     * Occurs when the request Content-Type is not supported by the endpoint.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.warn("Media type not supported: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
            "UNSUPPORTED_MEDIA_TYPE",
            "Content-Type application/json is required"
        );
        // 400 = HttpStatusCode.BAD_REQUEST
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplication(ApplicationException ex){

        log.warn("Application error: {}", ex.getDevMessage(), ex);

        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().name(),
            ex.getUserMessage()
        );
        // 400 = HttpStatusCode.BAD_REQUEST
        return ResponseEntity.status(400).body(response);
    }

    /**
     * Catches all unexpected exceptions that were not handled by any other handler.
     * This is the last resort to prevent unhandled exceptions from leaking to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);

        ErrorResponse response = new ErrorResponse(
            "INTERNAL_ERROR",
            "Error inesperado"
        );
        // 500 = HttpStatusCode.INTERNAL_SERVER_ERROR
        return ResponseEntity.status(500).body(response);
    }
}
