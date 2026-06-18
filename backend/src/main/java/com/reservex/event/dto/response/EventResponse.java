package com.reservex.event.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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
@Setter
@Builder

// Required by Jackson during Redis cache deserialization.
// Allows creation of an empty object before fields are populated.

/**
 * Required for Redis cache deserialization.
 *
 * Why?
 * EventResponse objects are stored in Redis as JSON using
 * GenericJackson2JsonRedisSerializer.
 *
 * When reading from Redis, Jackson first creates an empty
 * EventResponse object and then populates its fields.
 *
 * Without @NoArgsConstructor, Jackson cannot create the object,
 * causing:
 *
 * SerializationException:
 * "Cannot construct instance of EventResponse
 * (no Creators, like default constructor, exist)"
 */
@NoArgsConstructor

// Generates constructor with all fields.
// Useful for object creation and serialization frameworks.
@AllArgsConstructor
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