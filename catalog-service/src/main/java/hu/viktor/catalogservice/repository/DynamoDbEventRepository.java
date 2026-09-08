package hu.viktor.catalogservice.repository;

import hu.viktor.catalogservice.model.Event;
import hu.viktor.catalogservice.port.EventRepository;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DynamoDbEventRepository implements EventRepository {

    private final DynamoDbTemplate dynamoDbTemplate;

    @Override
    public Event save(Event event) {
        if (event.getId() == null) {
            event.setId(UUID.randomUUID());
        }
        return dynamoDbTemplate.save(event);
    }

    @Override
    public Optional<Event> findById(UUID id) {
        Event event = dynamoDbTemplate.load(
                software.amazon.awssdk.enhanced.dynamodb.Key.builder()
                        .partitionValue(id.toString())
                        .build(),
                Event.class
        );
        return Optional.ofNullable(event);
    }
}