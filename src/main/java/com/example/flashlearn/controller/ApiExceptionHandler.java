package com.example.flashlearn.controller;

import com.example.flashlearn.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of(
                "message",
                ex.getMessage() == null ? "Yeu cau khong hop le!" : ex.getMessage()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        if ("UNAUTHORIZED".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Vui long dang nhap!"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage() == null ? "Yeu cau khong hop le!" : ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnhandled(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "He thong tam thoi gian doan. Vui long thu lai!"));
    }
}
