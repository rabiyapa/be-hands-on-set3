package com.example.booking.meetingroom.exception;


import com.example.booking.meetingroom.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            String message,
            Map<String, String> errors) {

        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        message,
                        errors
                )
        );
    }

    @ExceptionHandler(BookingValidationException.class)
    public ResponseEntity<ErrorResponse> bookingValidation(
            BookingValidationException ex) {

        return response(HttpStatus.BAD_REQUEST,
                ex.getMessage(), ex.getErrors());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(
            ResourceNotFoundException ex) {

        return response(HttpStatus.NOT_FOUND,
                ex.getMessage(), Map.of());
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ErrorResponse> conflict(
            BookingConflictException ex) {

        return response(HttpStatus.CONFLICT,
                ex.getMessage(), Map.of());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unexpected(Exception ex) {

        return response(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error", Map.of());
    }
}