package com.example.eventsapp.controller;


import com.example.eventsapp.models.Event;
import com.example.eventsapp.models.Participant;
import com.example.eventsapp.repository.ParticipantRepository;
import com.example.eventsapp.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final EmailService emailService;

    private final ParticipantRepository participantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(ParticipantRepository participantRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService) {
        this.participantRepository = participantRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
     // Envoyer les rôles à la vue
        return "register"; // Retourne le fichier templates/register.html
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute Participant participant, @ModelAttribute Event event, HttpServletRequest request) {

        // Save the raw password before encoding it
        String rawPassword = participant.getPassword();

        // Encode the password and save the participant
        participant.setPassword(passwordEncoder.encode(rawPassword));
        participantRepository.save(participant);

        // Invalidate any existing session and clear the security context
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();

        // Authenticate the user with the raw password
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(participant.getUsername(), rawPassword)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            // Log the error and redirect to the login page or handle gracefully
            System.err.println("Authentication failed: " + e.getMessage());
            return "redirect:/home?error";
        }
        // Manually create a new session and bind the authentication
        session = request.getSession(true); // Create a new session
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());


        // Redirect based on the role
        if (participant.isAdmin()) {
            return "redirect:/organizer/events"; // Admin-specific page
        } else {
            return "redirect:/participant/events"; // User-specific page
        }


    }


    @GetMapping("/home")
    public String homePage(Model model, HttpServletRequest request) {
        boolean userLoggedIn = request.getSession().getAttribute("user") != null;
        model.addAttribute("userLoggedIn", userLoggedIn);
        model.addAttribute("requestURI", request.getRequestURI());

        return "home"; // Retourne la vue home.html
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Logique de déconnexion (par exemple, invalider la session)
        request.getSession().invalidate();
        return "redirect:/home";  // Redirige vers la page d'accueil après déconnexion
    }
}

