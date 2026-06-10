package com.reservex.event.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.reservex.event.enums.EventStatus;
import com.reservex.event.enums.EventCategory;
import com.reservex.show.entity.Show;
import java.util.List;
import java.time.Instant;
import java.util.UUID;

/**
 * Persistent event record.
 * Maps to the EVENTS table defined in the ReserveX ER diagram.
 *
 * Fields match the diagram exactly:
 *   id, title, description, category, language, event_status, duration_minutes, created_at, updated_at
 *
 * Event records are created and managed by admins.
 * Customers can only browse events through public APIs.
 */
@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventCategory category;

    @Column(nullable = false, length = 50)
    private String language;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false, length = 30)
    private EventStatus eventStatus;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(
        mappedBy = "event",
        cascade = CascadeType.ALL
    )
    private List<Show> shows;
}



