package com.reservex.event.mapper;

import com.reservex.common.util.DateUtil;
import com.reservex.event.dto.response.EventResponse;
import com.reservex.event.entity.Event;
import org.springframework.stereotype.Component;

/**
 * Converts Event entity → response DTO.
 */
@Component
public class EventMapper {

    public EventResponse toResponse(Event event) {

        return EventResponse.builder()
                .eventId(event.getId().toString())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory().name())
                .language(event.getLanguage())
                .eventStatus(event.getEventStatus().name())
                .durationMinutes(event.getDurationMinutes())
                .createdAt(DateUtil.formatUtc(event.getCreatedAt()))
                .updatedAt(DateUtil.formatUtc(event.getUpdatedAt()))
                .build();
    }
}