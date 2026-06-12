package com.reservex.inventory.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.reservex.inventory.entity.Seat;

@Repository
public interface SeatRepository
        extends JpaRepository<Seat, UUID> {
}
