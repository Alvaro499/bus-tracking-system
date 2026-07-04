package com.bustracking.shared.infrastructure.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ExternalServiceException;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.exception.ValidationException;

import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

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
                "Invalid parameter format");
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
                "Invalid JSON format or missing required fields");
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
                "Content-Type application/json is required");
        // 400 = HttpStatusCode.BAD_REQUEST
        return ResponseEntity.status(400).body(response);
    }

    /**
     * Catches all unexpected exceptions that were not handled by any other handler.
     * This is the last resort to prevent unhandled exceptions from leaking to the
     * client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);

        ErrorResponse response = new ErrorResponse(
                "INTERNAL_ERROR",
                "Error inesperado");
        // 500 = HttpStatusCode.INTERNAL_SERVER_ERROR
        return ResponseEntity.status(500).body(response);
    }

    // Own exceptions

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        log.warn("Not found: {}", ex.getDevMessage());
        ErrorResponse response = new ErrorResponse(
                ex.getErrorCode().name(),
                ex.getUserMessage());
        // 404 = HttpStatusCode.NOT_FOUND
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        log.warn("Validation error: {}", ex.getDevMessage());
        ErrorResponse response = new ErrorResponse(
                ex.getErrorCode().name(),
                ex.getUserMessage());
        // 400 = HttpStatusCode.BAD_REQUEST
        return ResponseEntity.status(400).body(response);
    }

    /**
     * Failed Authentication is an business rule violation. This because the user is
     * trying to access a resource without valid credentials.
     * Handles business rule violations, such as invalid credentials or other
     * domain-specific rules.
     * Returns 401 for invalid credentials and 422 for other business rule
     * violations.
     */

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex) {
        log.warn("Business rule violation: {}", ex.getDevMessage());

        int status = ex.getErrorCode() == ErrorCode.INVALID_CREDENTIALS
                ? 401 // Unauthorized - invalid credentials (username/password)
                : 422; // Unprocessable Entity or Logic - other business rule violations

        ErrorResponse response = new ErrorResponse(
                ex.getErrorCode().name(),
                ex.getDevMessage());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalService(ExternalServiceException ex) {
        log.error("External service error: {}", ex.getDevMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                ex.getErrorCode().name(),
                ex.getUserMessage());
        // 502 = HttpStatusCode.BAD_GATEWAY
        return ResponseEntity.status(502).body(response);
    }

    /**
     * Spring Security exceptions
     * Handles authentication and authorization errors.
     * Returns 401 for unauthenticated requests and 403 for unauthorized requests.
     * These new exceptions are thrown by Spring Security under the hood when authentication or authorization fails.
     */

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "UNAUTHORIZED", // invalid token
                "Autenticación requerida");
        // 401 = HttpStatusCode.UNAUTHORIZED
        return ResponseEntity.status(401).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "FORBIDDEN", // no rol matching 
                "Acceso denegado");
        // 403 = HttpStatusCode.FORBIDDEN
        return ResponseEntity.status(403).body(response);
    }

}
