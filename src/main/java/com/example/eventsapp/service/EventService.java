package com.example.eventsapp.service;

import com.example.eventsapp.models.Event;
import com.example.eventsapp.models.Participant;
import com.example.eventsapp.repository.EventRepository;
import com.example.eventsapp.repository.PaiementRepository;
import com.example.eventsapp.repository.ParticipantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final PaiementRepository paiementRepository;

    public EventService(EventRepository eventRepository, ParticipantRepository participantRepository, PaiementRepository paiementRepository) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.paiementRepository = paiementRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    public Optional<Event> getEventById(Long id) {
        Event event = eventRepository.findById(id).orElse(null); // Exemple de récupération d'un événement depuis un repository
        return Optional.ofNullable(event);
    }

    public List<Event> getEventsForParticipant(Long participantId) {
        return eventRepository.findByParticipantId(participantId);
    }
    // Dans EventService ou EventRepository
    public List<Event> getUpcomingEvents(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findAllByDateBetween(startDate, endDate);
    }




    public void removeParticipantFromWaitlist(Event event, Participant participant) {
        event.getWaitlist().remove(participant);
        eventRepository.save(event);
    }
    public String removeParticipantFromEvent(Long eventId, Participant participant) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Événement introuvable"));

        if (event.getParticipants().remove(participant)) {
            // Libérer une place
            if (!event.getWaitlist().isEmpty()) {
                Participant nextParticipant = event.getWaitlist().remove(0);
                event.getParticipants().add(nextParticipant);
            }
            eventRepository.save(event);
            return "Participant supprimé. Liste d'attente mise à jour.";
        } else if (event.getWaitlist().remove(participant)) {
            // Supprimer un participant de la liste d'attente
            eventRepository.save(event);
            return "Participant retiré de la liste d'attente.";
        }

        return "Participant non trouvé dans cet événement.";
    }


    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long eventId, Event updatedEvent) {
        return eventRepository.findById(eventId).map(event -> {
            event.setTitle(updatedEvent.getTitle());
            event.setDescription(updatedEvent.getDescription());
            event.setDate(updatedEvent.getDate());
            event.setLocation(updatedEvent.getLocation());
            event.setCapacity(updatedEvent.getCapacity());
            event.setPrice(updatedEvent.getPrice());
            return eventRepository.save(event);
        }).orElseThrow(() -> new RuntimeException("Événement non trouvé"));
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        paiementRepository.deleteByEventId(eventId);
        eventRepository.deleteById(eventId);
    }
}
