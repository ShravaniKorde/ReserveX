package com.reservex.event.repository;

import com.reservex.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repository for Event entity.
 *
 * Provides standard CRUD operations through JpaRepository:
 *   save()
 *   findById()
 *   findAll()
 *   deleteById()
 *   existsById()
 */
@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
}