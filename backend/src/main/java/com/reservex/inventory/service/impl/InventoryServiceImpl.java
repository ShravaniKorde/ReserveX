package com.reservex.inventory.service.impl;

import com.reservex.common.exception.AppException;
import com.reservex.inventory.dto.request.LockSeatsRequest;
import com.reservex.inventory.dto.response.LockSeatResponse;
import com.reservex.inventory.dto.response.LockedSeat;
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
import java.util.ArrayList;
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

        log.info("Fetching seats for showId={}", showId);

        List<ShowSeatResponse> seats =
                showSeatRepository.findByShow_Id(showId)
                        .stream()
                        .map(inventoryMapper::toResponse)
                        .toList();

        log.info(
                "Found {} seats for showId={}",
                seats.size(),
                showId
        );

        return seats;
    }

    @Override
    @Transactional
    public LockSeatResponse lockSeats(
            UUID showId,
            UUID userId,
            LockSeatsRequest request
    ) {

        log.info(
                "Seat lock request received. showId={}, userId={}",
                showId,
                userId
        );

        if (request.getSeatIds() == null ||
                request.getSeatIds().isEmpty()) {

            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "SEAT_IDS_REQUIRED",
                    "At least one seat must be selected"
            );
        }

        Instant now = Instant.now();

        List<LockedSeat> lockedSeats = new ArrayList<>();

        for (UUID seatId : request.getSeatIds()) {

            log.info(
                    "Attempting to lock seatId={} for showId={}",
                    seatId,
                    showId
            );

            ShowSeat showSeat =
                    showSeatRepository
                            .findByShow_IdAndSeat_Id(
                                    showId,
                                    seatId
                            )
                            .orElseThrow(() ->
                                    new AppException(
                                            HttpStatus.NOT_FOUND,
                                            "SEAT_NOT_FOUND",
                                            "Seat not found for show"
                                    )
                            );

            if (showSeat.getStatus() != SeatStatus.AVAILABLE) {

                log.warn(
                        "Seat unavailable. seatId={}, currentStatus={}",
                        seatId,
                        showSeat.getStatus()
                );

                throw new AppException(
                        HttpStatus.CONFLICT,
                        "SEAT_NOT_AVAILABLE",
                        "Seat is already locked or booked"
                );
            }

            showSeat.setStatus(SeatStatus.LOCKED);

            SeatLock savedLock =
                    seatLockRepository.save(
                            SeatLock.builder()
                                    .showSeat(showSeat)
                                    .userId(userId)
                                    .lockAcquiredAt(now)
                                    .lockExpiryAt(
                                            now.plusSeconds(300)
                                    )
                                    .lockStatus(
                                            LockStatus.ACTIVE
                                    )
                                    .build()
                    );

            lockedSeats.add(
                    LockedSeat.builder()
                            .lockId(
                                    savedLock.getId().toString()
                            )
                            .seatId(
                                    seatId.toString()
                            )
                            .build()
            );

            log.info(
                    "Seat locked successfully. seatId={}, lockId={}",
                    seatId,
                    savedLock.getId()
            );
        }

        log.info(
                "Seat locking completed. userId={}, totalLocks={}",
                userId,
                lockedSeats.size()
        );

        return LockSeatResponse.builder()
                .locks(lockedSeats)
                .expiresAt(
                        now.plusSeconds(300).toString()
                )
                .build();
    }

    @Override
    @Transactional
    public void releaseLock(UUID lockId) {

        log.info(
                "Lock release requested. lockId={}",
                lockId
        );

        SeatLock lock =
                seatLockRepository.findById(lockId)
                        .orElseThrow(() ->
                                new AppException(
                                        HttpStatus.NOT_FOUND,
                                        "LOCK_NOT_FOUND",
                                        "Lock not found"
                                )
                        );

        log.info(
                "Active lock found. lockId={}, seatId={}",
                lock.getId(),
                lock.getShowSeat().getId()
        );

        if (lock.getLockStatus() != LockStatus.ACTIVE) {

            log.warn(
                    "Lock already inactive. lockId={}, status={}",
                    lockId,
                    lock.getLockStatus()
            );

            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "LOCK_ALREADY_RELEASED",
                    "Lock is not active"
            );
        }

        lock.setLockStatus(
                LockStatus.RELEASED
        );

        ShowSeat seat = lock.getShowSeat();

        seat.setStatus(
                SeatStatus.AVAILABLE
        );

        seatLockRepository.save(lock);

        log.info(
                "Lock released successfully. lockId={}, seatId={}",
                lock.getId(),
                seat.getId()
        );
    }
}