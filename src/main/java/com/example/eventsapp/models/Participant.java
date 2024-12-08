package com.example.eventsapp.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Participant implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // Nom d'utilisateur pour l'authentification
    private String email;
    private String password;
    private boolean isAdmin;


    // Relation avec Event (si nécessaire)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "participant_event", // Table d'association
            joinColumns = @JoinColumn(name = "participant_id"), // Clé étrangère vers Participant
            inverseJoinColumns = @JoinColumn(name = "event_id") // Clé étrangère vers Event
    )
    private List<Event> events = new ArrayList<>();

    public Participant(Long id, String username, String email, String password, boolean isAdmin, List<Event> events) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.events = events;
    }

    public Participant() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}