package com.example.booking.meetingroom.repository;


import com.example.booking.meetingroom.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT b FROM Booking b
        WHERE b.room.id = :roomId
        AND b.cancelled = false
        AND b.startTime < :endTime
        AND b.endTime > :startTime
        ORDER BY b.startTime
        """)
    List<Booking> findConflicts(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    List<Booking> findByRoomIdAndStartTimeGreaterThanEqualAndStartTimeLessThanOrderByStartTimeAsc(
            Long roomId,
            LocalDateTime start,
            LocalDateTime end
    );
}