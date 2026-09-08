package hu.viktor.orderservice.port;

import hu.viktor.orderservice.model.Order;

public interface OrderEventPublisher {
    void publishOrderCreated(Order order);
}