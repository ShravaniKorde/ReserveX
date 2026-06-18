package com.reservex.booking.controller;

import com.reservex.booking.dto.response.BookingResponse;
import com.reservex.booking.service.BookingService;
import com.reservex.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /*
     * Create booking from locked seats.
     */
    @PostMapping("/shows/{showId}")
    public ResponseEntity<ApiResponse<BookingResponse>>
    createBooking(
            @PathVariable UUID showId,
            @AuthenticationPrincipal String userId
    ) {

        BookingResponse response =
                bookingService.createBooking(
                        UUID.fromString(userId),
                        showId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response));
    }

    /*
     * Fetch booking by ID.
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>>
    getBooking(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal String userId
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        bookingService.getBooking(
                                bookingId,
                                UUID.fromString(userId)
                        )
                )
        );
    }

    /*
     * Fetch all bookings of current user.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>>
    getBookings(
            @AuthenticationPrincipal String userId
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        bookingService.getBookings(
                                UUID.fromString(userId)
                        )
                )
        );
    }
}