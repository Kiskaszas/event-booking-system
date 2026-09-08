package hu.viktor.orderservice.dto;

public record OrderCreatedEvent(
        Long orderId,
        String eventId,
        String customerEmail,
        int quantity
) {
}
