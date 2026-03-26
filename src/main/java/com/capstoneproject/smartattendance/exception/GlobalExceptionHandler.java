package com.capstoneproject.smartattendance.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomeException.class)
    public ResponseEntity<?> handleCustomeException(CustomeException ex) {
        ErrorCode code = ex.getErrorCode();
        Map<String, Object> body = new HashMap<>();
        body.put("error", code.getMessage());
        return ResponseEntity
                .status(code.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "ALL_FIELD_REQUIRED");
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAnyException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity
                .status(ErrorCode.INTERNAL_ERROR.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("errorCode", "IO_ERROR");
        body.put("message", "File processing failed");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}