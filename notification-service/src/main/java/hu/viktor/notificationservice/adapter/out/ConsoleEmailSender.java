package hu.viktor.notificationservice.adapter.out;

import hu.viktor.notificationservice.port.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsoleEmailSender implements NotificationSender {

    @Override
    public void sendOrderConfirmation(String toEmail, String eventId, int quantity) {
        log.info("✉️ E-mail sikeresen kiküldve a következő címre: {} (Esemény: {}, Mennyiség: {} db)",
                toEmail, eventId, quantity);
        log.info("--------------------------------------------------");
    }
}