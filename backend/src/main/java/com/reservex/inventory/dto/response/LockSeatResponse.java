package com.reservex.inventory.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LockSeatResponse {

    private List<LockedSeat> locks;

    private String expiresAt;
}