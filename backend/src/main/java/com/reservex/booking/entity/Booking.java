package com.reservex.booking.entity;

import com.reservex.booking.enums.BookingStatus;
import com.reservex.show.entity.Show;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "show_id",
            nullable = false
    )
    private Show show;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "booking_status",
            nullable = false
    )
    private BookingStatus bookingStatus;

    @Column(
            name = "total_amount",
            nullable = false
    )
    private BigDecimal totalAmount;

    @Column(
            name = "booked_at",
            nullable = false
    )
    private Instant bookedAt;

    @CreationTimestamp
    @Column(
            name = "created_at",
            updatable = false
    )
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}