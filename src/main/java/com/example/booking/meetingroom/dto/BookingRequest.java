package com.example.booking.meetingroom.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public record BookingRequest(

        @NotNull
        Long roomId,

        @NotBlank
        String title,

        @NotBlank
        @Email
        String organiserEmail,

        @NotEmpty
        List<@NotBlank @Email String> attendeeEmails,

        @NotNull
        LocalDateTime startTime,

        @NotNull
        LocalDateTime endTime
) {}
