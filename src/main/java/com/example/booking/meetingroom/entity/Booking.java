package com.example.booking.meetingroom.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String organiserEmail;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "booking_attendees",
            joinColumns = @JoinColumn(name = "booking_id")
    )
    @Column(name = "email", nullable = false)
    private List<String> attendeeEmails;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private boolean cancelled;

    @PrePersist
    void onCreate() {
        createdTime = LocalDateTime.now();
    }
}