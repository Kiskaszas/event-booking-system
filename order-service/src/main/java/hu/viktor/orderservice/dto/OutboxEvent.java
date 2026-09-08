package hu.viktor.orderservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String aggregateId;
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime createdAt;
    private boolean processed;

    public static OutboxEvent create(String aggregateId, String eventType, String payload) {
        OutboxEvent event = new OutboxEvent();
        event.setAggregateId(aggregateId);
        event.setEventType(eventType);
        event.setPayload(payload);
        event.setCreatedAt(LocalDateTime.now());
        event.setProcessed(false);
        return event;
    }
}