package hu.viktor.orderservice.service;

import hu.viktor.orderservice.dto.CreateOrderRequest;
import hu.viktor.orderservice.model.Order;
import hu.viktor.orderservice.port.OrderEventPublisher;
import hu.viktor.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public Order placeOrder(CreateOrderRequest request) {
        Order order = Order.create(
                request.eventId(),
                request.customerEmail(),
                request.quantity()
        );

        Order savedOrder = orderRepository.save(order);
        log.info("Rendelés mentve Postgres-be: {}", savedOrder.getId());

        eventPublisher.publishOrderCreated(savedOrder);

        return savedOrder;
    }
}