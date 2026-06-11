package com.reservex.inventory.repository;

import com.reservex.inventory.entity.SeatLock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SeatLockRepository
        extends JpaRepository<SeatLock, UUID> {
}