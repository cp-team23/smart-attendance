package com.capstoneproject.smartattendance.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuthException(AuthException ex) {

        ErrorCode code = ex.getErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(Map.of(
                        "error", code.getMessage() // SAME AS BEFORE
                ));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(
            MethodArgumentNotValidException ex) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error", "ALL_FIELD_REQUIRED"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.getStatus())
                .body(Map.of(
                        "error", ErrorCode.INTERNAL_ERROR.getMessage()
                ));
    }
}
