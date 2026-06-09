package com.reservex.event.dto.request;

import com.reservex.event.entity.EventCategory;
import com.reservex.event.entity.EventStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request body for POST /api/v1/events
 *
 * Used by admins to create a new event in the system.
 *
 * Validation rules are enforced before the request reaches
 * the service layer.
 */
@Getter
@NoArgsConstructor
public class CreateEventRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    @Schema(
            description = "Event category",
            example = "MOVIE",
            allowableValues = {
                    "MOVIE",
                    "CONCERT",
                    "SPORTS",
                    "THEATRE",
                    "COMEDY",
                    "FESTIVAL",
                    "EXHIBITION"
            }
    )
    private EventCategory category;

    @NotBlank(message = "Language is required")
    private String language;

    @NotNull(message = "Event status is required")
    @Schema(
            description = "Current status of event",
            example = "ACTIVE",
            allowableValues = {
                    "ACTIVE",
                    "INACTIVE",
                    "CANCELLED"
            }
    )
    private EventStatus eventStatus;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be greater than zero")
    private Integer durationMinutes;
}