package com.example.eventsapp.controller;

import com.example.eventsapp.models.Event;
import com.example.eventsapp.models.Participant;
import com.example.eventsapp.repository.EventRepository;
import com.example.eventsapp.repository.ParticipantRepository;
import com.example.eventsapp.service.EventService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/organizer")
public class OrganizerWebController {

    private final EventService eventService;
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;

    public OrganizerWebController(EventService eventService, ParticipantRepository participantRepository, EventRepository eventRepository) {
        this.eventService = eventService;
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/events")
    public String getAllEvents(Model model, Authentication authentication) {
        // Récupérer l'organisateur connecté
        String username = authentication.getName();
        Participant participant = participantRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Organisateur introuvable"));

        // Charger les événements de l'organisateur
        List<Event> events = eventService.getAllEvents();

        // Vérifier si l'utilisateur est connecté
        boolean userLoggedIn = authentication.isAuthenticated();

        // Ajouter les données au modèle
        model.addAttribute("events", events);
        model.addAttribute("userLoggedIn", userLoggedIn);

        return "dashboard"; // Vue Thymeleaf
    }


    @GetMapping("/events/new")
    public String newEventForm(Model model) {
        model.addAttribute("event", new Event()); // Formulaire vide
        return "organizer/new-event"; // Vue Thymeleaf
    }

    @PostMapping("/events")
    public String createEvent(@ModelAttribute Event event, Authentication authentication) {
        String username = authentication.getName();
        Participant participant = participantRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Participant introuvable"));

        // Ajouter l'utilisateur authentifié comme participant à l'événement
        List<Participant> participants = new ArrayList<>();
        participants.add(participant);
        event.setParticipants(participants);

        // Créer l'événement
        eventService.createEvent(event);

        // Rediriger vers la liste des événements
        return "redirect:/organizer/events"; // Redirige vers la liste des événements
    }

    @GetMapping("/events/edit/{id}")
    public String editEventForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        model.addAttribute("event", event);
        return "organizer/edit-event"; // Vue Thymeleaf
    }

    @PostMapping("/edit-event/{id}")
    public String updateEvent(@PathVariable Long id, @ModelAttribute Event updatedEvent, RedirectAttributes redirectAttributes) {
        eventService.updateEvent(id, updatedEvent);
        redirectAttributes.addFlashAttribute("successMessage", "Événement modifié avec succès !");
        return "redirect:/organizer/events"; // Redirige vers la liste des événements
    }
    @Transactional
    @GetMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found"));

        // Dissocier les participants de l'événement
        for (Participant participant : event.getParticipants()) {
            participant.getEvents().remove(event);
        }

        // Mettre à jour les participants dans la base de données
        participantRepository.saveAll(event.getParticipants());

        // Dissocier la liste d'attente si nécessaire
        for (Participant participant : event.getWaitlist()) {
            participant.getEvents().remove(event);
        }
        participantRepository.saveAll(event.getWaitlist());

        // Supprimer l'événement
        eventRepository.delete(event);
        return "redirect:/organizer/events";
    }
    @GetMapping("/events/participants/{id}")
    public String viewParticipants(@PathVariable Long id, Model model) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement introuvable"));


        List<Participant> activeParticipants = event.getParticipants();

        List<Participant> waitlist = event.getWaitlist();
        model.addAttribute("event", event);
        model.addAttribute("activeParticipants", activeParticipants);
        model.addAttribute("waitlist", waitlist);
        return "event-participants"; // Vue à créer
    }
}
