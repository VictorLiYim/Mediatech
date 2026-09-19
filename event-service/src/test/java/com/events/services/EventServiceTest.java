package com.events.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.events.exceptions.AuthorNotFoundException;
import com.events.exceptions.EventNotFoundException;
import com.events.grpc.AuthorVerificationClient;
import com.events.models.Event;
import com.events.models.EventType;
import com.events.repositories.EventRepository;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private AuthorVerificationClient authorVerificationClient;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void createEvent_whenAuthorExists_savesEvent() {
        when(authorVerificationClient.authorExists(7L)).thenReturn(true);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event event = eventService.createEvent(
                "Braderie annuelle", "Dédicace", LocalDateTime.now().plusDays(1), EventType.DEDICATION, 7L, 30
        );

        assertThat(event.getTitle()).isEqualTo("Braderie annuelle");
        assertThat(event.getAuthorId()).isEqualTo(7L);
    }

    @Test
    void createEvent_whenAuthorDoesNotExist_throwsAuthorNotFoundAndNeverSaves() {
        when(authorVerificationClient.authorExists(7L)).thenReturn(false);

        assertThatThrownBy(() -> eventService.createEvent(
                "Braderie annuelle", "Dédicace", LocalDateTime.now().plusDays(1), EventType.DEDICATION, 7L, 30
        )).isInstanceOf(AuthorNotFoundException.class);

        verify(eventRepository, never()).save(any());
    }

    @Test
    void getAllEvents_returnsRepositoryResult() {
        Event event = new Event("Braderie", "desc", LocalDateTime.now(), EventType.DEDICATION, 7L, 30);
        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<Event> result = eventService.getAllEvents();

        assertThat(result).containsExactly(event);
    }

    @Test
    void getById_whenNotFound_throwsEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getById(1L))
                .isInstanceOf(EventNotFoundException.class);
    }
}
