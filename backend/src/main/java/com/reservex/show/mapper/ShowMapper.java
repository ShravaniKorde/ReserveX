package com.reservex.show.mapper;

import com.reservex.common.util.DateUtil;
import com.reservex.show.dto.response.ShowResponse;
import com.reservex.show.entity.Show;
import org.springframework.stereotype.Component;

/**
 * Converts Show entity to response DTO.
 */
@Component
public class ShowMapper {

    public ShowResponse toResponse(Show show) {

        return ShowResponse.builder()
                .showId(show.getId().toString())
                .eventId(show.getEvent().getId().toString())
                .screenId(show.getScreen().getId().toString())
                .startTime(DateUtil.formatUtc(show.getStartTime()))
                .endTime(DateUtil.formatUtc(show.getEndTime()))
                .status(show.getShow_status().name())
                .createdAt(DateUtil.formatUtc(show.getCreatedAt()))
                .updatedAt(DateUtil.formatUtc(show.getUpdatedAt()))
                .build();
    }
}