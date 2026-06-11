package com.reservex.inventory.entity;

import com.reservex.inventory.enums.LockStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "seat_locks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatLock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "show_seat_id",
            nullable = false
    )
    private ShowSeat showSeat;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "lock_acquired_at", nullable = false)
    private Instant lockAcquiredAt;

    @Column(name = "lock_expiry_at", nullable = false)
    private Instant lockExpiryAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "lock_status", nullable = false)
    private LockStatus lockStatus;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}