package hu.viktor.orderservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
        @NotBlank String eventId,
        @Email @NotBlank String customerEmail,
        @Min(1) int quantity
) {
}
