package com.reservex.show.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Response body for Show APIs.
 */
@Getter
@Builder
public class ShowResponse {

    private String showId;

    private String eventId;

    private String screenId;

    private String startTime;

    private String endTime;

    private String status;

    private String createdAt;

    private String updatedAt;
}