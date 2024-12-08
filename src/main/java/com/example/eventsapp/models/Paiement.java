package com.example.eventsapp.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;
    private String methode; // Exemple : "Carte de Crédit", "PayPal"
    private String statut;  // Exemple : "SUCCÈS", "ÉCHEC"
    private LocalDateTime datePaiement;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne
    @JoinColumn(name = "evenement_id", nullable = false)
    private Event event;

    public Paiement(Long id, Double montant, String methode, String statut, LocalDateTime datePaiement, Participant participant, Event event) {
        this.id = id;
        this.montant = montant;
        this.methode = methode;
        this.statut = statut;
        this.datePaiement = datePaiement;
        this.participant = participant;
        this.event = event;
    }

    public Paiement() {

    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getMethode() {
        return methode;
    }

    public void setMethode(String methode) {
        this.methode = methode;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
