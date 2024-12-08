package com.example.eventsapp.repository;

import com.example.eventsapp.models.Event;
import com.example.eventsapp.models.Paiement;
import com.example.eventsapp.models.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByParticipantAndEvent(Participant participant, Event event);
    @Modifying
    @Query("DELETE FROM Paiement p WHERE p.event.id = :eventId")
    void deleteByEventId(@Param("eventId") Long eventId);
}
