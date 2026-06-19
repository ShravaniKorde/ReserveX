package com.reservex.event.service;

import com.reservex.event.dto.request.CreateEventRequest;
import com.reservex.event.dto.request.UpdateEventRequest;
import com.reservex.event.dto.response.EventListResponse;
import com.reservex.event.dto.response.EventResponse;

import java.util.List;
import java.util.UUID;

/**
 * Event service contract.
 * Controller depends on this interface, not the implementation.
 */

public interface EventService {

    /** Create a new event. */
    EventResponse createEvent(CreateEventRequest request);

    /** Return a single event by ID. */
    EventResponse getEvent(UUID eventId);

    /** Return all available events. */
    EventListResponse getAllEvents();

    /** Update an existing event. */
    EventResponse updateEvent(UUID eventId, UpdateEventRequest request);


    /** Delete an event by ID. */
    void deleteEvent(UUID eventId);

    /** Get event details using show ID. */
    EventResponse getEventByShowId(UUID showId);
}