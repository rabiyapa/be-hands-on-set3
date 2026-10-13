package com.example.booking.meetingroom.exception;

import java.util.Map;

public class BookingValidationException
        extends RuntimeException {

    private final Map<String, String> errors;

    public BookingValidationException(Map<String, String> errors) {
        super("Booking validation failed");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
