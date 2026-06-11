package com.reservex.inventory.controller;

import com.reservex.common.response.ApiResponse;
import com.reservex.inventory.dto.response.ShowSeatResponse;
import com.reservex.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/shows/{showId}/seats")
    public ResponseEntity<ApiResponse<List<ShowSeatResponse>>> getSeats(
            @PathVariable UUID showId
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        inventoryService.getSeats(showId)
                )
        );
    }
}