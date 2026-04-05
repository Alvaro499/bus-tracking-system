package com.bustracking.admin.infrastructure.web.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bustracking.shared.exception.ApplicationException;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ExternalServiceException;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.exception.ValidationException;
import com.bustracking.shared.infrastructure.error.ErrorResponse;

// Every module should have its own exception handler, to avoid coupling between modules and to be able to handle exceptions in a specific way for each module.
@RestControllerAdvice
public class AdminExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AdminExceptionHandler.class);


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex){

        log.warn("Not found: {}", ex.getDevMessage());

        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().name(),
            ex.getUserMessage()
        );
        // 404 = HttpStatusCode.NOT_FOUND
        return ResponseEntity.status(404).body(response);

    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex){

        log.warn("Validation error: {}", ex.getDevMessage());

        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().name(),
            ex.getUserMessage()
        );
        // 400 = HttpStatusCode.BAD_REQUEST
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex){

        log.warn("Business rule violation: {}", ex.getDevMessage());

        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().name(),
            ex.getUserMessage()
        );
        // 422 = HttpStatusCode.UNPROCESSABLE_ENTITY
        return ResponseEntity.status(422).body(response);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalService(ExternalServiceException ex){

        log.error("External service error: {}", ex.getDevMessage(), ex);

        ErrorResponse response = new ErrorResponse(
            ex.getErrorCode().name(),
            ex.getUserMessage()
        );
        // 502 = HttpStatusCode.BAD_GATEWAY
        return ResponseEntity.status(502).body(response);
    }

}
