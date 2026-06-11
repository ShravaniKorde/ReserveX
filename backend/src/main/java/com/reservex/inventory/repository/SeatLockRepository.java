package com.reservex.inventory.repository;

import com.reservex.inventory.entity.SeatLock;
import com.reservex.inventory.enums.LockStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface SeatLockRepository
        extends JpaRepository<SeatLock, UUID> {

                /*Used to check:
                * Is this seat already locked? */

        Optional<SeatLock> findByShowSeat_IdAndLockStatus(
                UUID showSeatId,
                LockStatus lockStatus
        );
}