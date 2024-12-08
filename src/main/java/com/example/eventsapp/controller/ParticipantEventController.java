package com.example.eventsapp.controller;

import com.example.eventsapp.models.Event;
import com.example.eventsapp.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant/events")
public class ParticipantEventController {

    private final EventService eventService;

    public ParticipantEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getAvailableEvents() {
        return eventService.getAllEvents();
    }

    // Vous pouvez ajouter ici une méthode pour gérer l'inscription et les paiements
}
