package com.example.eventsapp.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDate date;
    private String location;
    private int capacity; // Nombre maximum de participants
    private double price; // Prix de participation

    // Relation avec l'organisateur
    @ManyToMany(mappedBy = "events", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Participant> participants = new ArrayList<>();// Organisateur de l'événement

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "event_waitlist",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<Participant> waitlist = new ArrayList<>();


    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Paiement> paiements = new ArrayList<>();

    public Event(String title, Long id, String description, LocalDate date, String location, int capacity, double price, List<Participant> participants, List<Participant> waitlist, List<Paiement> paiements) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.date = date;
        this.location = location;
        this.capacity = capacity;
        this.price = price;
        this.participants = participants;
        this.waitlist = waitlist;
        this.paiements = paiements;
    }

    public Event() {

    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<Participant> getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(List<Participant> waitlist) {
        this.waitlist = waitlist;
    }
    public void addParticipant(Participant participant) {
        this.participants.add(participant);
        participant.getEvents().add(this);
    }

    public void addToWaitlist(Participant participant) {
        this.waitlist.add(participant);

    }

    public List<Paiement> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<Paiement> paiements) {
        this.paiements = paiements;
    }
}