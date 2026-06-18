package com.reservex.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
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