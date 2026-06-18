package com.reservex.event.service.impl;

import com.reservex.common.exception.AppException;
import com.reservex.event.dto.request.CreateEventRequest;
import com.reservex.event.dto.request.UpdateEventRequest;
import com.reservex.event.dto.response.EventResponse;
import com.reservex.event.entity.Event;
import com.reservex.event.mapper.EventMapper;
import com.reservex.event.repository.EventRepository;
import com.reservex.event.service.EventService;
import com.reservex.show.repository.ShowRepository;
import com.reservex.show.entity.Show;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ShowRepository showRepository;

     // ── POST /api/v1/events ───────────────────────────────────────────────────
    @CacheEvict(value = "events", allEntries = true)
    @Override
    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {

        log.info("Creating event: {}", request.getTitle());

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .language(request.getLanguage())
                .eventStatus(request.getEventStatus())
                .durationMinutes(request.getDurationMinutes())
                .build();

        Event saved = eventRepository.save(event);

        log.info("Event created: {} ({})", saved.getTitle(), saved.getId());
        log.info("Evicting 'events' cache after event creation");

        return eventMapper.toResponse(saved);
    }

     // ── GET /api/v1/events/{eventId} ─────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEvent(UUID eventId) {

        Event event = getEventOrThrow(eventId);

        return eventMapper.toResponse(event);
    }

     // ── GET /api/v1/events ───────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {

        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toResponse)
                .toList();
    }

    // ── PUT /api/v1/events/{eventId} ─────────────────────────────────────────

    @Override
    @Transactional
    public EventResponse updateEvent(
            UUID eventId,
            UpdateEventRequest request
    ) {

        Event event = getEventOrThrow(eventId);

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setLanguage(request.getLanguage());
        event.setEventStatus(request.getEventStatus());
        event.setDurationMinutes(request.getDurationMinutes());

        Event updated = eventRepository.save(event);

        log.info("Event updated: {} ({})", updated.getTitle(), updated.getId());

        return eventMapper.toResponse(updated);
    }

    // ── DELETE /api/v1/events/{eventId} ──────────────────────────────────────

    @Caching(evict = {
                @CacheEvict(value = "events", allEntries = true),
                @CacheEvict(value = "eventByShow", allEntries = true)
        })
    @Override
    @Transactional
    public void deleteEvent(UUID eventId) {

        log.info("Deleting event with id={}", eventId);
        Event event = getEventOrThrow(eventId);

        eventRepository.delete(event);

        log.info("Event deleted: {} ({})", event.getTitle(), event.getId());
        log.info("Evicting caches: events, eventByShow");
    }

     // ── shared lookup helper ─────────────────────────────────────────────────

    /**
     * Returns an event if found.
     *
     * Throws:
     *   404 NOT_FOUND if event does not exist.
     */
    private Event getEventOrThrow(UUID eventId) {

        return eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new AppException(
                                HttpStatus.NOT_FOUND,
                                "EVENT_NOT_FOUND",
                                "Event not found"
                        )
                );
    }

    /**
    * Get event details using show ID.
    */
   @Cacheable(value = "eventByShow", key = "#showId")
    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventByShowId(UUID showId) {

        log.info("Cache MISS - Fetching event from DB for showId={}", showId);

        Show show = showRepository.findById(showId)
                .orElseThrow(() ->
                        new AppException(
                                HttpStatus.NOT_FOUND,
                                "SHOW_NOT_FOUND",
                                "Show not found"
                        )
                );

        return eventMapper.toResponse(show.getEvent());
    }    
}