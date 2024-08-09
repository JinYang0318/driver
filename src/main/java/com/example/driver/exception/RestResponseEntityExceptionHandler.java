package com.example.driver.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private ResponseEntity<Object> getErrorMessageRequest(HttpStatus httpStatus, Exception e) {
        return new ResponseEntity<>(ErrorMessage.builder().message(e.getMessage()).build(), httpStatus);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        logger.error("Not found: {}", e);
        return getErrorMessageRequest(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(UniqueException.class)
    public ResponseEntity<Object> handleUniqueException(UniqueException e) {
        logger.error("Unique Exception: {}", e);
        return getErrorMessageRequest(HttpStatus.CONFLICT, e);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String errorMessage = String.format("Validation error for field '%s': %s", error.getField(), error.getDefaultMessage());
            logger.error(errorMessage);
        });

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
