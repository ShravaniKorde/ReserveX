package com.reservex.event.dto.response;

import lombok.Builder;
import lombok.Getter;


/**
 * Response body for Event APIs.
 *
 * Used by:
 *   GET  /api/v1/events
 *   GET  /api/v1/events/{eventId}
 *   POST /api/v1/events
 *   PUT  /api/v1/events/{eventId}
 *
 * Shape:
 * {
 *   "event_id":         "f47ac10b-58cc-4372-a567-0e02b2c3d479",
 *   "title":            "Avengers Endgame",
 *   "description":      "Marvel superhero movie",
 *   "category":         "MOVIE",
 *   "language":         "English",
 *   "event_status":     "ACTIVE",
 *   "duration_minutes": 180,
 *   "created_at":       "2026-06-01T10:00:00Z",
 *   "updated_at":       "2026-06-01T10:00:00Z"
 * }
 */
@Getter
@Builder
public class EventResponse {

    private String eventId;

    private String title;

    private String description;

    private String category;

    private String language;

    private String eventStatus;

    private Integer durationMinutes;

    private String createdAt;

    private String updatedAt;
}