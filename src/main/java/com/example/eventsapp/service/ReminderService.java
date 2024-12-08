package com.example.eventsapp.service;

import com.example.eventsapp.models.Event;
import com.example.eventsapp.models.Participant;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReminderService {

    private final EmailService emailService;
    private final EventService eventService;
    private final ParticipantDetailsService participantDetailsService;

    public ReminderService(EmailService emailService, EventService eventService, ParticipantDetailsService participantDetailsService) {
        this.emailService = emailService;
        this.eventService = eventService;
        this.participantDetailsService = participantDetailsService;
    }

    @Scheduled(cron = "0 30 22 * * *")
    public void sendEventReminder() {
        // Définir la période pour les rappels (par exemple, les événements dans les 24 heures)
        LocalDate now = LocalDate.now();
        LocalDate upcomingWindow = now.plusDays(1);

        // Récupérer les événements à venir
        List<Event> upcomingEvents = eventService.getUpcomingEvents(now, upcomingWindow);

        for (Event event : upcomingEvents) {
            // Récupérer les participants inscrits pour chaque événement
            List<Participant> participants = participantDetailsService.getParticipantsByEventId(event.getId());
            // Envoyer un rappel à chaque participant
            for (Participant participant : participants) {
                String subject = "Rappel : Événement à venir - " + event.getTitle();
                String body = "Bonjour " + participant.getUsername() + ",\n\n"
                        + "Nous vous rappelons que l'événement \"" + event.getTitle()
                        + "\" commence le " + event.getDate() + ".\n"
                        + "Merci de votre participation.\n\n"
                        + "Cordialement,\n"
                        + "L'équipe d'organisation";

                emailService.sendEmail(participant.getEmail(), subject, body);
            }
        }
    }

}

