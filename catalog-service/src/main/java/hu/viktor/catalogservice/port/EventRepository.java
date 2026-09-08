package hu.viktor.catalogservice.port;

import hu.viktor.catalogservice.model.Event;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository {
    Event save(Event event);
    Optional<Event> findById(UUID id);
}