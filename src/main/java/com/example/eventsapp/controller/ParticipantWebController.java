package com.example.eventsapp.controller;

import com.example.eventsapp.models.Event;
import com.example.eventsapp.models.Paiement;
import com.example.eventsapp.models.Participant;
import com.example.eventsapp.repository.PaiementRepository;
import com.example.eventsapp.repository.ParticipantRepository;
import com.example.eventsapp.service.EventService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/participant")
public class ParticipantWebController {

    private final EventService eventService;
    private final ParticipantRepository participantRepository;
    private final PaiementRepository paiementRepository;

    public ParticipantWebController(EventService eventService, ParticipantRepository participantRepository, PaiementRepository paiementRepository) {
        this.eventService = eventService;
        this.participantRepository = participantRepository;
        this.paiementRepository = paiementRepository;
    }

    @GetMapping("/events")
    public String getAvailableEvents(Model model, Authentication authentication) {
        List<Event> events = eventService.getAllEvents();
        boolean userLoggedIn = authentication.isAuthenticated();
        model.addAttribute("events", events);
        model.addAttribute("userLoggedIn", userLoggedIn);
        return "participant/events"; // Vue Thymeleaf
    }

    @PostMapping("/events/pay/{id}")
    public String payForEvent(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Participant participant = participantRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Participant non trouvé"));

        // Vérifier si le participant a déjà payé
        Optional<Paiement> existingPayment = paiementRepository.findByParticipantAndEvent(participant, event);
        if (existingPayment.isPresent()) {
            model.addAttribute("paiement", existingPayment.get());
            model.addAttribute("message", "Vous avez déjà payé pour cet événement.");
            return "participant/payment-confirmation"; // Vue confirmation
        }

        Paiement paiement = new Paiement();
        paiement.setMontant(event.getPrice()); // Exemple : utilisez le prix de l'événement
        paiement.setMethode("En attente"); // À remplir plus tard après le traitement
        paiement.setStatut("En attente"); // Statut initial
        paiement.setParticipant(participant);
        paiement.setEvent(event);

        boolean userLoggedIn = authentication.isAuthenticated();

        // Afficher la vue de paiement
        model.addAttribute("paiement", paiement);
        model.addAttribute("event", event);
        model.addAttribute("participant", participant);
        model.addAttribute("userLoggedIn", userLoggedIn);

        return "participant/payment";
    }


}
