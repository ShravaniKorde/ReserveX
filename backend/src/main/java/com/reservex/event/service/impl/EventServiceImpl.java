package com.reservex.event.service.impl;

import com.reservex.common.exception.AppException;
import com.reservex.event.dto.request.CreateEventRequest;
import com.reservex.event.dto.request.UpdateEventRequest;
import com.reservex.event.dto.response.EventListResponse;
import com.reservex.event.dto.response.EventResponse;
import com.reservex.event.entity.Event;
import com.reservex.event.mapper.EventMapper;
import com.reservex.event.repository.EventRepository;
import com.reservex.event.service.EventService;
import com.reservex.show.entity.Show;
import com.reservex.show.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ShowRepository showRepository;

     // ── POST /api/v1/events ───────────────────────────────────────────────────
    @Override
    @CacheEvict(value = "events:list", allEntries = true)
    @Transactional
    public EventResponse createEvent(
            CreateEventRequest request
    ) {

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

     // ── GET /api/v1/events/{eventId} ───────────────────────────────────────────────────
    @Override
    @Cacheable(value = "events:id", key = "#eventId")
    @Transactional(readOnly = true)
    public EventResponse getEvent(
            UUID eventId
    ) {

        log.info(
              "Cache MISS - Fetching event from DB. eventId={}",
                eventId
        );

        Event event = getEventOrThrow(eventId);

        return eventMapper.toResponse(event);
    }

     // ── GET /api/v1/events ───────────────────────────────────────────────────
    @Override
    @Cacheable(value = "events:list", key = "'all'")
@Transactional(readOnly = true)
public EventListResponse getAllEvents() {

    log.info("Cache MISS - Fetching all events from DB");

    return EventListResponse.builder()
            .events(
                    eventRepository.findAll()
                            .stream()
                            .map(eventMapper::toResponse)
                            .toList()
            )
            .build();
}


 // ── PUT /api/v1/events ───────────────────────────────────────────────────
    @Override
@Caching(evict = {
    @CacheEvict(value = "events:list", allEntries = true),
    @CacheEvict(value = "events:id", key = "#eventId"),
    @CacheEvict(value = "events:show",allEntries = true)
})
        
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
        event.setDurationMinutes(
                request.getDurationMinutes()
        );

        Event updated =
                eventRepository.save(event);

        log.info(
                "Event updated: {} ({})",
                updated.getTitle(),
                updated.getId()
        );

        return eventMapper.toResponse(updated);
    }

    // ── DELETE /api/v1/events/{eventId} ──────────────────────────────────────
    
    @Override
    @Caching(evict = {
    @CacheEvict(value = "events:list", allEntries = true),
    @CacheEvict(value = "events:id", key = "#eventId")
})
    @Transactional
    public void deleteEvent(
            UUID eventId
    ) {

        log.info("Deleting event with id={}", eventId);
        Event event = getEventOrThrow(eventId);

        if (showRepository.existsByEventId(eventId)) {

                   log.warn(
            "Delete failed. Event {} has associated shows",
            eventId
    );

    throw new AppException(
            HttpStatus.BAD_REQUEST,
            "EVENT_HAS_SHOWS",
            "Cannot delete event with existing shows"
    );
}

        eventRepository.delete(event);

        log.info(
                "Event deleted: {} ({})",
                event.getTitle(),
                event.getId()
        );
    }

    private Event getEventOrThrow(
            UUID eventId
    ) {

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
    @Cacheable(value = "events:show", key = "#showId")
    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventByShowId(
            UUID showId
    ) {

        log.info("Cache MISS - Fetching event from DB for showId={}", showId);

        Show show = showRepository.findById(showId)
                .orElseThrow(() ->
                        new AppException(
                                HttpStatus.NOT_FOUND,
                                "SHOW_NOT_FOUND",
                                "Show not found"
                        )
                );

        return eventMapper.toResponse(
                show.getEvent()
        );
    }
}