package hu.viktor.orderservice.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.viktor.orderservice.dto.OrderCreatedEvent;
import hu.viktor.orderservice.model.Order;
import hu.viktor.orderservice.model.OutboxEvent;
import hu.viktor.orderservice.port.OrderEventPublisher;
import hu.viktor.orderservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxOrderEventPublisher implements OrderEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getEventId(),
                order.getCustomerEmail(),
                order.getQuantity()
        );

        try {
            String payload = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = OutboxEvent.create(
                    String.valueOf(order.getId()),
                    "OrderCreatedEvent",
                    payload
            );
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Hiba az esemény szerializálása során", e);
        }
    }
}