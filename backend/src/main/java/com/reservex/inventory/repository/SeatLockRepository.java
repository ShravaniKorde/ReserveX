package com.reservex.inventory.repository;

import com.reservex.inventory.entity.SeatLock;
import com.reservex.inventory.enums.LockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface SeatLockRepository
        extends JpaRepository<SeatLock, UUID> {

                /*Used to check:
                * Is this seat already locked? */

        Optional<SeatLock> findByShowSeat_IdAndLockStatus(
                UUID showSeatId,
                LockStatus lockStatus
        );

        //Added for booking service impl to find all active seat locks for user and show
        List<SeatLock> findByUserIdAndShowSeat_Show_IdAndLockStatus(
                UUID userId,
                UUID showId,
                LockStatus lockStatus
        );

        List<SeatLock> findByLockStatus(
        LockStatus lockStatus
        );
}