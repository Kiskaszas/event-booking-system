package hu.viktor.notificationservice.service;

import hu.viktor.notificationservice.dto.OrderCreatedEvent;
import hu.viktor.notificationservice.port.NotificationSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OrderNotificationServiceTest {

    @Mock
    private NotificationSender notificationSender;

    @InjectMocks
    private OrderNotificationService notificationService;

    @Test
    void shouldProcessOrderAndSendEmail() {
        // Given
        OrderCreatedEvent event = new OrderCreatedEvent(
                1L,
                "evt-123",
                "test@example.com",
                2
        );

        // When
        notificationService.processOrderNotification(event);

        // Then
        verify(notificationSender, times(1))
                .sendOrderConfirmation("test@example.com", "evt-123", 2);
    }
}