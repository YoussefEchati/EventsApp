package com.example.eventsapp.component;

import com.example.eventsapp.models.Participant;
import com.example.eventsapp.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ParticipantRepository participantRepository;

    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ParticipantRepository participantRepository, PasswordEncoder passwordEncoder) {
        this.participantRepository = participantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Vérifiez si l'ADMIN existe déjà
        if (participantRepository.findByUsername("admin").isEmpty()) {
            Participant admin = new Participant();
            admin.setUsername("admin");
            admin.setEmail("youssef-echati@outlook.com");
            admin.setPassword(passwordEncoder.encode("12345")); // Remplacez "password" par un vrai mot de passe sécurisé
            admin.setIsAdmin(true); // Définit cet utilisateur comme ADMIN
            participantRepository.save(admin);
        }
    }
}
