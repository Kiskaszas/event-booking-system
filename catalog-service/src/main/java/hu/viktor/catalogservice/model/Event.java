package hu.viktor.catalogservice.model;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@DynamoDbBean
@Getter
@Setter
public class Event {

    private UUID id;
    private String title;
    private LocalDateTime date;
    private int availableSeats;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("eventId")
    public UUID getId() {
        return id;
    }

    public static Event create(String title, LocalDateTime date, int availableSeats) {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setTitle(title);
        event.setDate(date);
        event.setAvailableSeats(availableSeats);
        return event;
    }
}