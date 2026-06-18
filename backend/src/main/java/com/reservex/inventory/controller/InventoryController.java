package com.reservex.inventory.controller;

import com.reservex.common.response.ApiResponse;
import com.reservex.inventory.dto.request.LockSeatsRequest;
import com.reservex.inventory.dto.response.LockSeatResponse;
import com.reservex.inventory.dto.response.ShowSeatResponse;
import com.reservex.inventory.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

        @GetMapping("/shows/{showId}/seats")
        public ResponseEntity<ApiResponse<List<ShowSeatResponse>>> getSeats( @PathVariable UUID showId) {

                return ResponseEntity.ok(
                        ApiResponse.ok(
                                inventoryService.getSeats(showId)
                        )
                );
        }

        /*Purpose:
        User selects seats

        A1
        A2
        A3
        ↓
        System locks them for 5 minutes
        ↓
        Nobody else can book them */

        @PostMapping("/shows/{showId}/seats/lock")
        public ResponseEntity<ApiResponse<LockSeatResponse>> lockSeats(@PathVariable UUID showId, @AuthenticationPrincipal String userId, @Valid @RequestBody LockSeatsRequest request) {
                LockSeatResponse response =
                inventoryService.lockSeats(
                        showId,
                        UUID.fromString(userId),
                        request
                );

                return ResponseEntity.ok(
                ApiResponse.ok(response)
                );
        }

        /*Purpose:

        User closes browser
        or
        Booking cancelled
        ↓
        Release seat */

        @DeleteMapping("/locks/{lockId}")
        public ResponseEntity<Void> releaseLock( @PathVariable UUID lockId) {

                inventoryService.releaseLock(lockId);

                return ResponseEntity.noContent().build();
        }
}