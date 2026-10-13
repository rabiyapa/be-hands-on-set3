package com.example.booking.meetingroom.controller;

import com.example.booking.meetingroom.dto.BookingRequest;
import com.example.booking.meetingroom.dto.BookingResponse;
import com.example.booking.meetingroom.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/api/bookings")
    public ResponseEntity<BookingResponse> create(
            @Valid @RequestBody BookingRequest request) {

        BookingResponse response = bookingService.create(request);

        return ResponseEntity
                .created(URI.create("/api/bookings/" + response.id()))
                .body(response);
    }

    @GetMapping("/api/bookings/{id}")
    public BookingResponse get(@PathVariable Long id) {
        return bookingService.get(id);
    }

    @GetMapping("/api/rooms/{roomId}/bookings")
    public List<BookingResponse> list(
            @PathVariable Long roomId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return bookingService.listByRoom(roomId, date);
    }

    @DeleteMapping("/api/bookings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        bookingService.cancel(id);
    }
}