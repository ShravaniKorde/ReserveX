package com.reservex.event.controller;

import com.reservex.common.response.ApiResponse;
import com.reservex.event.dto.request.CreateEventRequest;
import com.reservex.event.dto.request.UpdateEventRequest;
import com.reservex.event.dto.response.EventResponse;
import com.reservex.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * Handles:
 *
 * POST   /api/v1/events
 * GET    /api/v1/events
 * GET    /api/v1/events/{eventId}
 * PUT    /api/v1/events/{eventId}
 * DELETE /api/v1/events/{eventId}
 */
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // ─────────────────────────────────────────────────────────
    // POST /api/v1/events
    // ─────────────────────────────────────────────────────────

    /**
     * Create a new event.
     *
     * Returns 201 Created on success.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(
            @Valid @RequestBody CreateEventRequest request
    ) {

        EventResponse response = eventService.createEvent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response));
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/v1/events
    // ─────────────────────────────────────────────────────────

    /**
     * Fetch all events.
     *
     * Returns 200 OK.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> getAllEvents() {

        List<EventResponse> response = eventService.getAllEvents();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ─────────────────────────────────────────────────────────
    // GET /api/v1/events/{eventId}
    // ─────────────────────────────────────────────────────────

    /**
     * Fetch event by ID.
     *
     * Returns 200 OK.
     * Returns 404 Not Found if event doesn't exist.
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventResponse>> getEvent(
            @PathVariable UUID eventId
    ) {

        EventResponse response = eventService.getEvent(eventId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ─────────────────────────────────────────────────────────
    // PUT /api/v1/events/{eventId}
    // ─────────────────────────────────────────────────────────

    /**
     * Update an existing event.
     *
     * Returns 200 OK.
     * Returns 404 Not Found if event doesn't exist.
     */
    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventRequest request
    ) {

        EventResponse response =
                eventService.updateEvent(eventId, request);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ─────────────────────────────────────────────────────────
    // DELETE /api/v1/events/{eventId}
    // ─────────────────────────────────────────────────────────

    /**
     * Delete an event.
     *
     * Returns 204 No Content.
     * Returns 404 Not Found if event doesn't exist.
     */
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable UUID eventId
    ) {

        eventService.deleteEvent(eventId);

        return ResponseEntity.noContent().build();
    }
}