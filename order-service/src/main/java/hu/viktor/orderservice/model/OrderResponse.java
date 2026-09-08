package hu.viktor.orderservice.dto;

import hu.viktor.orderservice.model.Order;

public record OrderResponse(Long id, String status) {
    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(order.getId(), order.getStatus());
    }
}