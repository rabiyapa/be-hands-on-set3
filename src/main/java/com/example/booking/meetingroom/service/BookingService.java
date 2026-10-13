package com.example.booking.meetingroom.service;



import com.example.booking.meetingroom.dto.BookingRequest;
import com.example.booking.meetingroom.dto.BookingResponse;
import com.example.booking.meetingroom.entity.Booking;
import com.example.booking.meetingroom.entity.Room;
import com.example.booking.meetingroom.exception.BookingConflictException;
import com.example.booking.meetingroom.exception.BookingValidationException;
import com.example.booking.meetingroom.exception.ResourceNotFoundException;
import com.example.booking.meetingroom.repository.BookingRepository;
import com.example.booking.meetingroom.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public BookingResponse create(BookingRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        LocalDateTime start = request.startTime();
        LocalDateTime end = request.endTime();

        if (start != null && end != null) {

            if (!start.toLocalDate().equals(end.toLocalDate())) {
                errors.put("endTime", "Booking must be on a single day");
            }

            if (!start.isBefore(end)) {
                errors.put("endTime", "End must be after start");
            }

            if (start.toLocalTime().isBefore(LocalTime.of(9, 0))
                    || start.toLocalTime().isAfter(LocalTime.of(18, 0))) {
                errors.put("startTime", "Must be within 09:00 to 18:00");
            }

            if (end.toLocalTime().isAfter(LocalTime.of(18, 0))
                    || end.toLocalTime().isBefore(LocalTime.of(9, 0))) {
                errors.put("endTime", "Must be within 09:00 to 18:00");
            }

            if (start.getMinute() % 15 != 0
                    || start.getSecond() != 0
                    || start.getNano() != 0) {
                errors.put("startTime", "Must use 15-minute steps");
            }

            if (end.getMinute() % 15 != 0
                    || end.getSecond() != 0
                    || end.getNano() != 0) {
                errors.put("endTime", "Must use 15-minute steps");
            }

            if (Duration.between(start, end).compareTo(Duration.ofHours(4)) > 0) {
                errors.put("duration", "Maximum booking duration is 4 hours");
            }

            if (start.isBefore(LocalDateTime.now())) {
                errors.put("startTime", "Booking cannot be in the past");
            }
        }

        if (request.attendeeEmails() != null
                && request.organiserEmail() != null
                && !request.attendeeEmails().contains(request.organiserEmail())) {
            errors.put("attendeeEmails", "Organiser must be an attendee");
        }

        if (!errors.isEmpty()) {
            throw new BookingValidationException(errors);
        }

        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found: " + request.roomId()));

        if (!room.isActive()) {
            errors.put("roomId", "Room is inactive");
        }

        if (request.attendeeEmails().size() > room.getCapacity()) {
            errors.put("attendeeEmails", "Room capacity exceeded");
        }

        if (!errors.isEmpty()) {
            throw new BookingValidationException(errors);
        }

        List<Booking> conflicts =
                bookingRepository.findConflicts(room.getId(), start, end);

        if (!conflicts.isEmpty()) {
            Booking existing = conflicts.get(0);
            throw new BookingConflictException(
                    "Clashes with booking " + existing.getId()
                            + ": " + existing.getTitle());
        }

        Booking booking = Booking.builder()
                .room(room)
                .title(request.title())
                .organiserEmail(request.organiserEmail())
                .attendeeEmails(new ArrayList<>(request.attendeeEmails()))
                .startTime(start)
                .endTime(end)
                .cancelled(false)
                .build();

        return toResponse(bookingRepository.saveAndFlush(booking));
    }

    @Transactional(readOnly = true)
    public BookingResponse get(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found: " + id));

        return toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listByRoom(Long roomId, LocalDate date) {

        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found: " + roomId);
        }

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        return bookingRepository
                .findByRoomIdAndStartTimeGreaterThanEqualAndStartTimeLessThanOrderByStartTimeAsc(
                        roomId, from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void cancel(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found: " + id));

        booking.setCancelled(true);
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getRoom().getId(),
                b.getRoom().getName(),
                b.getTitle(),
                b.getOrganiserEmail(),
                List.copyOf(b.getAttendeeEmails()),
                b.getStartTime(),
                b.getEndTime(),
                b.getCreatedTime(),
                b.isCancelled()
        );
    }
}