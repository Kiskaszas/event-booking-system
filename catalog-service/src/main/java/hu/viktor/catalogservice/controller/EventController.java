package hu.viktor.catalogservice.controller;

import hu.viktor.catalogservice.dto.CreateEventRequest;
import hu.viktor.catalogservice.dto.EventResponse;
import hu.viktor.catalogservice.model.Event;
import hu.viktor.catalogservice.port.EventRepository;
import hu.viktor.catalogservice.service.EventService;
import hu.viktor.catalogservice.service.S3PosterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final S3PosterService s3PosterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody CreateEventRequest request) {
        Event savedEvent = eventService.createEvent(request);
        return mapToResponse(savedEvent);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable String eventId) {
        Optional<Event> eventOpt = eventRepository.findById(UUID.fromString(eventId));
        return eventOpt.map(event -> ResponseEntity.ok(mapToResponse(event)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/{eventId}/poster", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponse> uploadPoster(
            @PathVariable String eventId,
            @RequestParam("file") MultipartFile file) throws IOException {

        Optional<Event> eventOpt = eventRepository.findById(UUID.fromString(eventId));

        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = eventOpt.get();
        String posterUrl = s3PosterService.uploadPoster(file);

        Event updatedEvent = eventRepository.save(event);

        return ResponseEntity.ok(mapToResponse(updatedEvent));
    }

    private EventResponse mapToResponse(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new EventResponse(
                event.getId().toString(),
                event.getTitle(),
                event.getDate().format(formatter),
                event.getAvailableSeats()
        );
    }
}