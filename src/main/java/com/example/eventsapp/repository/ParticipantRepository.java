package com.example.eventsapp.repository;

import com.example.eventsapp.models.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Optional<Participant> findByUsername(String username);
    @Query("SELECT p FROM Participant p JOIN p.events e WHERE e.id = :eventId")
    List<Participant> findAllByEventId(@Param("eventId") Long eventId);
}
