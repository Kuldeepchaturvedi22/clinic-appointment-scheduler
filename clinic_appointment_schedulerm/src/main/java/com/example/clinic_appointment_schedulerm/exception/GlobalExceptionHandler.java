package com.example.clinic_appointment_schedulerm.exception;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.LazyInitializationException;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiError(
                OffsetDateTime.now(), 400, "Bad Request", ex.getMessage(), req.getRequestURI()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleConflict(IllegalStateException ex, HttpServletRequest req) {
        log.warn("Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(
                OffsetDateTime.now(), 409, "Conflict", ex.getMessage(), req.getRequestURI()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ApiError(
                OffsetDateTime.now(), 400, "Validation Failed", msg, req.getRequestURI()
        ));
    }

    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(java.util.NoSuchElementException ex, HttpServletRequest req) {
        log.warn("Not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(
                OffsetDateTime.now(), 404, "Not Found", ex.getMessage(), req.getRequestURI()
        ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(
                OffsetDateTime.now(), 401, "Authentication Failed", "You are not registered. Please register first.", req.getRequestURI()
        ));
    }

    @ExceptionHandler({HttpMessageConversionException.class, InvalidDefinitionException.class})
    public ResponseEntity<ApiError> handleSerializationError(Exception ex, HttpServletRequest req) {
        log.error("Serialization error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(
                OffsetDateTime.now(), 400, "Serialization Error", ex.getMessage(), req.getRequestURI()
        ));
    }

    @ExceptionHandler(LazyInitializationException.class)
    public ResponseEntity<ApiError> handleLazyInitialization(LazyInitializationException ex, HttpServletRequest req) {
        log.error("Lazy initialization error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(
                OffsetDateTime.now(), 500, "Data Loading Error", ex.getMessage(), req.getRequestURI()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(
                OffsetDateTime.now(), 500, "Internal Server Error", "An unexpected error occurred", req.getRequestURI()
        ));
    }

}