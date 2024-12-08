package com.example.eventsapp.controller;


import com.example.eventsapp.models.Event;
import com.example.eventsapp.models.Paiement;
import com.example.eventsapp.models.Participant;
import com.example.eventsapp.repository.EventRepository;
import com.example.eventsapp.repository.ParticipantRepository;
import com.example.eventsapp.service.EmailService;
import com.example.eventsapp.service.EventService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.example.eventsapp.repository.PaiementRepository;
import com.example.eventsapp.service.StripeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final StripeService stripeService;
    private final PaiementRepository paiementRepository;
    private final EventService eventService;
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final EmailService emailService;

    public PaymentController(StripeService stripeService, PaiementRepository paiementRepository, EventService eventService, ParticipantRepository participantRepository, EventRepository eventRepository, EmailService emailService) {
        this.stripeService = stripeService;
        this.paiementRepository = paiementRepository;
        this.eventService = eventService;
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.emailService = emailService;
    }
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @RequestParam Long eventId,
            @RequestParam Long participantId,
            @RequestParam double price,
            @RequestParam String eventName) {
        try {
            // Appeler le service pour créer une session Stripe
            String sessionUrl = stripeService.createCheckoutSession(eventName, eventId, participantId, price);

            // Retourner l'URL de session au client
            Map<String, String> response = new HashMap<>();
            response.put("url", sessionUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Journaliser l'erreur
            logger.error("Erreur lors de la création de la session de paiement.", e);

            // Retourner une réponse avec un statut d'erreur
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur lors de la création de la session de paiement.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/success")
    public String confirmPayment(@RequestParam Long eventId, @RequestParam Long participantId, Model model, Authentication authentication) {
        try {
            // Récupérer l'événement
            Event event = eventService.getEventById(eventId)
                    .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

            // Récupérer le participant
            Participant participant = participantRepository.findById(participantId)
                    .orElseThrow(() -> new RuntimeException("Participant non trouvé"));

            // Vérifiez si la capacité est atteinte
            if ( event.getCapacity() > 0) {
                // Ajouter le participant à l'événement
                event.addParticipant(participant);
                event.setCapacity(event.getCapacity() - 1);
                 // Mettre à jour l'événement dans la base
            } else {
                // Ajouter à la liste d'attente si la capacité est dépassée
                event.addToWaitlist(participant);
                // Mettre à jour l'événement dans la base
            }
            eventRepository.save(event);
            // Enregistrer les détails du paiement
            Paiement paiement = new Paiement();
            paiement.setEvent(event);
            paiement.setParticipant(participant);
            paiement.setMontant(event.getPrice());
            paiement.setMethode("Carte bancaire"); // ou une autre méthode choisie
            paiement.setStatut("Payé");
            paiementRepository.save(paiement);

            String participantEmail = participant.getEmail();  // Vous récupérez l'email du participant
            String eventName = event.getTitle();

            String subject = "Confirmation de votre inscription à l'événement";
            String body = "Bonjour, vous êtes inscrit à l'événement : " + eventName;
            emailService.sendEmail(participantEmail, subject, body);

            // Ajouter des informations au modèle pour la vue
            model.addAttribute("event", event);
            model.addAttribute("participant", participant);
            model.addAttribute("paiement", paiement);
            model.addAttribute("userLoggedIn", authentication.isAuthenticated());

            return "payment-confirmation"; // Redirige vers la vue HTML "payment-confirmation"
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la confirmation du paiement : " + e.getMessage());
            return "payment-error"; // Page d'erreur si nécessaire
        }
    }


    @GetMapping("/cancel")
    public String handlePaymentCancel(Model model) {
        model.addAttribute("error", "Votre paiement a été annulé. Veuillez réessayer ou contacter le support.");
        return "payment-error";
    }

}