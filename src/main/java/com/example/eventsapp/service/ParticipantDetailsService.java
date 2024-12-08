package com.example.eventsapp.service;

import com.example.eventsapp.models.Participant;
import com.example.eventsapp.repository.ParticipantRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class ParticipantDetailsService implements UserDetailsService {

    private final ParticipantRepository participantRepository;

    public ParticipantDetailsService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Participant participant=participantRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable avec le nom : " + username));



        return new org.springframework.security.core.userdetails.User(
                participant.getUsername(),
                participant.getPassword(),
                getAuthorities(participant)
        );
    }
    // Dans ParticipantService ou ParticipantRepository
    public List<Participant> getParticipantsByEventId(Long eventId) {
        return participantRepository.findAllByEventId(eventId);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Participant participant) {
        // Si l'utilisateur est ADMIN, on lui donne le rôle ROLE_ADMIN
        if (participant.isAdmin()) {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            // Sinon, il est un simple utilisateur (ROLE_USER)
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

}
