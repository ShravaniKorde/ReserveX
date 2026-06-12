package com.reservex.inventory.service;

import com.reservex.inventory.dto.request.LockSeatsRequest;
import com.reservex.inventory.dto.response.LockSeatResponse;
import com.reservex.inventory.dto.response.ShowSeatResponse;

import java.util.List;
import java.util.UUID;

public interface InventoryService {

    List<ShowSeatResponse> getSeats(UUID showId);

    /**
    * Lock selected seats for a user.
    *
    * Returns lock details and expiry time.
    */
    LockSeatResponse lockSeats(
            UUID showId,
            UUID userId,
            LockSeatsRequest request
    );

    void releaseLock(UUID lockId);
    
}