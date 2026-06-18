package com.reservex.inventory.repository;

import com.reservex.inventory.entity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShowSeatRepository
        extends JpaRepository<ShowSeat, UUID> {

    List<ShowSeat> findByShow_Id(UUID showId);

    Optional<ShowSeat> findByShow_IdAndSeat_Id(
        UUID showId,
        UUID seatId
    );
}