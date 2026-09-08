package hu.viktor.notificationservice.service;

import hu.viktor.notificationservice.dto.OrderCreatedEvent;
import hu.viktor.notificationservice.port.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNotificationService {

    private final NotificationSender notificationSender;

    public void processOrderNotification(OrderCreatedEvent event) {
        log.info("Feldolgozás alatt lévő rendelés: {}", event.orderId());
        notificationSender.sendOrderConfirmation(
                event.customerEmail(),
                event.eventId(),
                event.quantity()
        );
    }
}