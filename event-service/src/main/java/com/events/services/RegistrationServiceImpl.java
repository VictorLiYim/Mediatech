package com.events.services;

import com.events.exceptions.AlreadyRegisteredException;
import com.events.exceptions.EventFullException;
import com.events.models.Event;
import com.events.models.Registration;
import com.events.repositories.RegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService{


    private static final Logger log = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    private final RegistrationRepository registrationRepository;
    private final EventService eventService;

    public RegistrationServiceImpl(RegistrationRepository registrationRepository, EventService eventService){
        this.registrationRepository = registrationRepository;
        this.eventService = eventService;
    }
    @Override
    @Transactional
    public Registration register(Long eventId, Long userId){
        Event event = eventService.getById(eventId);

        if(registrationRepository.existsByEventIdAndUserId(eventId, userId)){
            throw new AlreadyRegisteredException(eventId, userId);
        }

        long currentRegistrations = registrationRepository.countByEventId(eventId);
        if(currentRegistrations>=event.getCapacity()){
            throw new EventFullException(eventId);
        }

        Registration saved = registrationRepository.save(new Registration(eventId, userId));
        log.info("User {} registered to event {}", userId, eventId);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Registration> getRegistrationsForUser(Long userId){
        return registrationRepository.findByUserId(userId);
    }
}
