package com.example.eventsapp.controller;

import com.example.eventsapp.models.Event;
import com.example.eventsapp.models.Participant;
import com.example.eventsapp.repository.ParticipantRepository;
import com.example.eventsapp.service.EventService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/organizer/events")
public class OrganizerEventController {

    private final EventService eventService;
    private final ParticipantRepository participantRepository;

    public OrganizerEventController(EventService eventService, ParticipantRepository participantRepository) {
        this.eventService = eventService;
        this.participantRepository = participantRepository;
    }

    @GetMapping
    public List<Event> getOrganizerEvents(Authentication authentication) {
        // Récupérer l'organisateur connecté
        String username = authentication.getName();
        Participant participant = participantRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organisateur introuvable"));
        return eventService.getEventsForParticipant(participant.getId());
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event, Authentication authentication) {
        String username = authentication.getName();
        Participant participant = participantRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Participant introuvable"));

        // Ajouter l'utilisateur authentifié comme participant à l'événement
        List<Participant> participants = new ArrayList<>();
        participants.add(participant);
        event.setParticipants(participants);

        // Créer l'événement
        return eventService.createEvent(event);
    }

    @PutMapping("/{eventId}")
    public Event updateEvent(@PathVariable Long eventId, @RequestBody Event updatedEvent) {
        return eventService.updateEvent(eventId, updatedEvent);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
    }
}
