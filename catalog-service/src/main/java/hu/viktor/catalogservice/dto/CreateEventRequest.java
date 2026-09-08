package hu.viktor.catalogservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateEventRequest(
        @NotBlank(message = "A cím megadása kötelező")
        String title,

        @NotBlank(message = "A dátum megadása kötelező")
        String date,

        @Min(value = 1, message = "A szabad helyek számának legalább 1-nek kell lennie")
        int availableSeats
) {}