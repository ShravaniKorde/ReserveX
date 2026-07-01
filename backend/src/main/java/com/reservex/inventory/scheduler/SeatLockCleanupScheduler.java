package com.reservex.inventory.scheduler;

import com.reservex.inventory.entity.SeatLock;
import com.reservex.inventory.entity.ShowSeat;
import com.reservex.inventory.enums.LockStatus;
import com.reservex.inventory.enums.SeatStatus;
import com.reservex.inventory.repository.SeatLockRepository;
import com.reservex.inventory.service.SeatLockRedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatLockCleanupScheduler {

    private final SeatLockRepository seatLockRepository;
    private final SeatLockRedisService seatLockRedisService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredLocks() {

        List<SeatLock> activeLocks =
                seatLockRepository.findByLockStatus(
                        LockStatus.ACTIVE
                );

        for (SeatLock lock : activeLocks) {

            UUID showId =
                    lock.getShowSeat()
                            .getShow()
                            .getId();

            UUID seatId =
                    lock.getShowSeat()
                            .getSeat()
                            .getId();

            boolean redisLockExists =
                    seatLockRedisService.isLocked(
                            showId,
                            seatId
                    );

            if (!redisLockExists) {

                ShowSeat showSeat =
                        lock.getShowSeat();

                showSeat.setStatus(
                        SeatStatus.AVAILABLE
                );

                lock.setLockStatus(
                        LockStatus.EXPIRED
                );

                log.info(
                        "Expired seat lock cleaned up. lockId={}, seatId={}",
                        lock.getId(),
                        seatId
                );
            }
        }
    }
}