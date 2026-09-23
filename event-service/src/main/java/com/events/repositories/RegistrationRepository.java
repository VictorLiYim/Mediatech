package com.events.repositories;

import com.events.models.Registration;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends CrudRepository<Registration, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<Registration> findByEventIdAndUserId(Long eventId, Long userId);

    long countByEventId(Long eventId);

    List<Registration> findByUserId(Long userId);
}
