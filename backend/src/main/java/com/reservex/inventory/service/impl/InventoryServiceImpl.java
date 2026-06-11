package com.reservex.inventory.service.impl;

import com.reservex.common.exception.AppException;
import com.reservex.inventory.dto.request.LockSeatsRequest;
import com.reservex.inventory.dto.response.LockSeatResponse;
import com.reservex.inventory.dto.response.ShowSeatResponse;
import com.reservex.inventory.entity.SeatLock;
import com.reservex.inventory.entity.ShowSeat;
import com.reservex.inventory.enums.LockStatus;
import com.reservex.inventory.enums.SeatStatus;
import com.reservex.inventory.mapper.InventoryMapper;
import com.reservex.inventory.repository.SeatLockRepository;
import com.reservex.inventory.repository.ShowSeatRepository;
import com.reservex.inventory.service.InventoryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ShowSeatRepository showSeatRepository;
    private final InventoryMapper inventoryMapper;
    private final SeatLockRepository seatLockRepository;

    @Override
    public List<ShowSeatResponse> getSeats(UUID showId) {

        // return showSeatRepository.findByShow_Id(showId)
        // .stream()
        // .map(inventoryMapper::toResponse)
        // .toList();

        log.info("Fetching seats for showId={}", showId);
        List<ShowSeatResponse> seats = showSeatRepository.findByShow_Id(showId)
            .stream()
            .map(inventoryMapper::toResponse)
            .toList();

        log.info("Found {} seats for showId={}", seats.size(), showId);
        return seats;
    }

    @Override
    @Transactional
    public LockSeatResponse lockSeats(UUID showId, UUID userId, LockSeatsRequest request) {
        
        log.info("Seat lock request received. showId={}, userId={}", showId, userId);
         
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
        
            throw new AppException(
                HttpStatus.BAD_REQUEST,
                "SEAT_IDS_REQUIRED",
                "At least one seat must be selected"
            );
        }   

        if (request.getSeatIds().size() > 1) {

            throw new AppException(
                HttpStatus.BAD_REQUEST,
                "MULTIPLE_SEATS_NOT_SUPPORTED",
                "Only one seat can be locked per request"
            );
        }

        Instant now = Instant.now();

        UUID seatId = request.getSeatIds().get(0);
            log.info( "Attempting to lock seatId={} for showId={}", seatId, showId);
            
            ShowSeat showSeat =
                    showSeatRepository
                            .findByShow_IdAndSeat_Id(showId, seatId)
                            .orElseThrow(() ->
                                    new AppException(
                                            HttpStatus.NOT_FOUND,
                                            "SEAT_NOT_FOUND",
                                            "Seat not found for show"
                                    )
                                );

            if (showSeat.getStatus() != SeatStatus.AVAILABLE) {

                log.warn("Seat unavailable. seatId={}, currentStatus={}", seatId, showSeat.getStatus());

                throw new AppException(
                        HttpStatus.CONFLICT,
                        "SEAT_NOT_AVAILABLE",
                        "Seat is already locked or booked"
                );
            }

            showSeat.setStatus(SeatStatus.LOCKED);
            // Hibernate dirty checking will persist this change automatically when transaction commits.

            SeatLock lock = SeatLock.builder()
                    .showSeat(showSeat)
                    .userId(userId)
                    .lockAcquiredAt(now)
                    .lockExpiryAt(now.plusSeconds(300))
                    .lockStatus(LockStatus.ACTIVE)
                    .build();

            SeatLock savedLock = seatLockRepository.save(lock);

        log.info("Seat locked successfully. seatId={}, lockId={}, expiresAt={}", seatId, savedLock.getId(), savedLock.getLockExpiryAt());

        log.info("Seat locking completed. userId={}, lockId={}", userId, savedLock.getId());

        return LockSeatResponse.builder()
            .lockId(savedLock.getId().toString())
            .expiresAt(savedLock.getLockExpiryAt().toString())
            .build();
    }

    @Override
    @Transactional
    public void releaseLock(UUID lockId) {
        log.info("Lock release requested. lockId={}", lockId);
        SeatLock lock =
                seatLockRepository.findById(lockId)
                        .orElseThrow(() ->
                                new AppException(
                                    HttpStatus.NOT_FOUND,
                                    "LOCK_NOT_FOUND",
                                    "Lock not found"
                                )
                        );

        log.info( "Active lock found. lockId={}, seatId={}", lock.getId(), lock.getShowSeat().getId());
        
        if (lock.getLockStatus() != LockStatus.ACTIVE) {

            log.warn("Lock already inactive. lockId={}, status={}",lockId,lock.getLockStatus());

            throw new AppException(
                HttpStatus.BAD_REQUEST,
                "LOCK_ALREADY_RELEASED",
                "Lock is not active"
            );
        }

        lock.setLockStatus(LockStatus.RELEASED);
        ShowSeat seat = lock.getShowSeat();
        seat.setStatus(SeatStatus.AVAILABLE);
        // Hibernate dirty checking will persist this change automatically when transaction commits.
        seatLockRepository.save(lock);
        log.info("Lock released successfully. lockId={}, seatId={}",lock.getId(),seat.getId());
    }
}