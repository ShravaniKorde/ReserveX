package com.reservex.inventory.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LockedSeat {

    private String lockId;

    private String seatId;
}