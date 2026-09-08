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

// A @DynamoDbBean és a @DynamoDbPartitionKey kötelező a Spring Cloud AWS
// DynamoDbTemplate (Enhanced Client) számára, enélkül a save() 500-as hibát dob.
@DynamoDbBean
@Getter
@Setter
public class Event {

    private UUID id;
    private String title;
    private LocalDateTime date;
    private int availableSeats;

    // Lombok-nak nem adunk annotációt közvetlenül a mezőn, ezért itt manuálisan
    // felülírjuk a getId()-t, hogy rá tudjuk tenni a DynamoDB annotációkat.
    // A tábla partition key-je "eventId" néven jött létre a setup-aws.sh-ban,
    // ezért kell a @DynamoDbAttribute("eventId") is.
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