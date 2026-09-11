package com.events.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.events.dto.RegisterRequest;
import com.events.dto.RegistrationResponse;
import com.events.services.RegistrationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/events")
public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/{id}/register")
    public RegistrationResponse register(@PathVariable Long id, @Valid @RequestBody RegisterRequest request) {
        log.info("POST /events/{}/register userId={}", id, request.userId());
        return RegistrationResponse.from(registrationService.register(id, request.userId()));
    }

    @GetMapping("/registrations")
    public List<RegistrationResponse> getRegistrationsForUser(@RequestParam Long userId) {
        log.info("GET /events/registrations userId={}", userId);
        return registrationService.getRegistrationsForUser(userId).stream()
                .map(RegistrationResponse::from)
                .toList();
    }
}