package com.reservex.show.repository;

import com.reservex.show.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShowRepository
        extends JpaRepository<Show, UUID> {
}