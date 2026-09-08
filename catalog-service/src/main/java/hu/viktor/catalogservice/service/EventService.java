package hu.viktor.catalogservice.service;

import hu.viktor.catalogservice.dto.CreateEventRequest;
import hu.viktor.catalogservice.model.Event;
import hu.viktor.catalogservice.port.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(CreateEventRequest request) {
        Event event = Event.create(
                request.title(),
                LocalDateTime.parse(request.date()),
                request.availableSeats()
        );

        Event savedEvent = eventRepository.save(event);
        log.info("Új esemény regisztrálva a katalógusban: {} (ID: {}, Jegyek: {})",
                savedEvent.getTitle(), savedEvent.getId(), savedEvent.getAvailableSeats());

        return savedEvent;
    }
}