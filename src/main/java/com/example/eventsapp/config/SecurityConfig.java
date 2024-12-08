package com.example.eventsapp.config;

import com.example.eventsapp.service.ParticipantDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class SecurityConfig {

    private final ParticipantDetailsService participantDetailsService;

    public SecurityConfig(ParticipantDetailsService participantDetailsService) {
        this.participantDetailsService = participantDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/home", "/register", "/css/**", "/js/**").permitAll()
                                .requestMatchers("/dashboard").hasRole("ADMIN")
                                .requestMatchers("/participant/**").hasRole("USER")
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/home")
                                .successHandler(authenticationSuccessHandler())
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/home")
                                .permitAll()
                );

        return http.build();

    }
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();
            String redirectUrl = "/";

            if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                redirectUrl = "/organizer/events"; // Page des organisateurs
            } else if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))) {
                redirectUrl = "/participant/events"; // Page des participants
            }

            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encodage des mots de passe
    }

}
