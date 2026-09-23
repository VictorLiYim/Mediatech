package com.events.services;

import com.events.models.Registration;

import java.util.List;

public interface RegistrationService {
    Registration register(Long eventId, Long userId);
    void unregister(Long eventId, Long userId);
    List<Registration> getRegistrationsForUser(Long userId);
}
