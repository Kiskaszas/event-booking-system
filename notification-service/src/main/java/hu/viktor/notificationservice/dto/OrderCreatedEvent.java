package hu.viktor.notificationservice.dto;

public record OrderCreatedEvent(
        Long orderId,
        String eventId,
        String customerEmail,
        int quantity
) {}