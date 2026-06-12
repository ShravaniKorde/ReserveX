package com.reservex.show.entity;

import com.reservex.event.entity.Event;
import com.reservex.screen.entity.Screen;
import com.reservex.show.enums.ShowStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Persistent show record.
 *
 * Maps to SHOWS table from ER diagram.
 *
 * Fields:
 * id
 * event_id
 * venue_id
 * start_time
 * end_time
 * status
 * created_at
 * updated_at
 */
@Entity
@Table(name = "shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    /**
     * Many shows belong to one event.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "event_id",
            nullable = false
    )
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "screen_id",
        nullable = false
    )
    private Screen screen;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShowStatus show_status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}