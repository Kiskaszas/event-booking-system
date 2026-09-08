package hu.viktor.catalogservice.dto;

public record EventResponse(
        String eventId,
        String title,
        String date,
        int availableSeats
) {}
