package com.example.booking.meetingroom.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id,
        Long roomId,
        String roomName,
        String title,
        String organiserEmail,
        List<String> attendeeEmails,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime createdTime,
        boolean cancelled
) {}
