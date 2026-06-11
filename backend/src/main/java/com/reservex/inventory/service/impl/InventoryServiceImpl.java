package com.reservex.inventory.service.impl;

import com.reservex.inventory.dto.response.ShowSeatResponse;
import com.reservex.inventory.mapper.InventoryMapper;
import com.reservex.inventory.repository.ShowSeatRepository;
import com.reservex.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ShowSeatRepository showSeatRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    public List<ShowSeatResponse> getSeats(UUID showId) {

        return showSeatRepository.findByShow_Id(showId)
        .stream()
        .map(inventoryMapper::toResponse)
        .toList();
    }
}