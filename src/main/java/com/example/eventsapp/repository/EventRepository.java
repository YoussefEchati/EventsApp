package com.example.eventsapp.repository;

import com.example.eventsapp.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e JOIN e.participants p WHERE p.id = :participantId")
    List<Event> findByParticipantId(@Param("participantId") Long participantId);
    List<Event> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}
