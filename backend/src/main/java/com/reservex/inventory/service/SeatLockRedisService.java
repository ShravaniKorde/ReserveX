package com.reservex.inventory.service;

import java.util.UUID;

public interface SeatLockRedisService {

    boolean lockSeat(
            UUID showId,
            UUID seatId,
            UUID userId
    );

    void unlockSeat(
            UUID showId,
            UUID seatId
    );

    boolean isLocked(
            UUID showId,
            UUID seatId
    );

    String getLockOwner(
            UUID showId,
            UUID seatId
    );
}