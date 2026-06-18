package com.reservex.inventory.dto.request;

import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LockSeatsRequest {

    @NotEmpty(message = "At least one seat must be selected")
    private List<UUID> seatIds;
}