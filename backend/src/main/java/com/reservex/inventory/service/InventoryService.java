package com.reservex.inventory.service;

import com.reservex.inventory.dto.response.ShowSeatResponse;

import java.util.List;
import java.util.UUID;

public interface InventoryService {

    List<ShowSeatResponse> getSeats(UUID showId);
}