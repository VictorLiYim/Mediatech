package com.events.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.events.exceptions.AlreadyRegisteredException;
import com.events.exceptions.EventFullException;
import com.events.exceptions.UserNotFoundException;
import com.events.grpc.UserVerificationClient;
import com.events.models.Event;
import com.events.models.EventType;
import com.events.models.Registration;
import com.events.repositories.RegistrationRepository;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventService eventService;

    @Mock
    private UserVerificationClient userVerificationClient;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private Event sampleEvent() {
        return new Event("Braderie", "desc", LocalDateTime.now().plusDays(1), EventType.DEDICATION, 7L, 2);
    }

    @Test
    void register_whenUserExistsAndRoomLeft_savesRegistration() {
        when(userVerificationClient.userExists(42L)).thenReturn(true);
        when(eventService.getById(1L)).thenReturn(sampleEvent());
        when(registrationRepository.existsByEventIdAndUserId(1L, 42L)).thenReturn(false);
        when(registrationRepository.countByEventId(1L)).thenReturn(0L);
        when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Registration result = registrationService.register(1L, 42L);

        assertThat(result.getEventId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(42L);
    }

    @Test
    void register_whenUserDoesNotExist_throwsUserNotFoundAndNeverTouchesEvent() {
        when(userVerificationClient.userExists(42L)).thenReturn(false);

        assertThatThrownBy(() -> registrationService.register(1L, 42L))
                .isInstanceOf(UserNotFoundException.class);

        verify(eventService, never()).getById(any());
    }

    @Test
    void register_whenAlreadyRegistered_throwsAlreadyRegistered() {
        when(userVerificationClient.userExists(42L)).thenReturn(true);
        when(eventService.getById(1L)).thenReturn(sampleEvent());
        when(registrationRepository.existsByEventIdAndUserId(1L, 42L)).thenReturn(true);

        assertThatThrownBy(() -> registrationService.register(1L, 42L))
                .isInstanceOf(AlreadyRegisteredException.class);
    }

    @Test
    void register_whenEventFull_throwsEventFull() {
        when(userVerificationClient.userExists(42L)).thenReturn(true);
        when(eventService.getById(1L)).thenReturn(sampleEvent());
        when(registrationRepository.existsByEventIdAndUserId(1L, 42L)).thenReturn(false);
        when(registrationRepository.countByEventId(1L)).thenReturn(2L);

        assertThatThrownBy(() -> registrationService.register(1L, 42L))
                .isInstanceOf(EventFullException.class);
    }

    @Test
    void getRegistrationsForUser_returnsRepositoryResult() {
        Registration registration = new Registration(1L, 42L);
        when(registrationRepository.findByUserId(42L)).thenReturn(List.of(registration));

        List<Registration> result = registrationService.getRegistrationsForUser(42L);

        assertThat(result).containsExactly(registration);
    }
}
