package hu.viktor.notificationservice.adapter.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.viktor.notificationservice.dto.OrderCreatedEvent;
import hu.viktor.notificationservice.service.OrderNotificationService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsOrderEventListener {

    private final ObjectMapper objectMapper;
    private final OrderNotificationService notificationService;

    @SqsListener("notification-queue")
    public void processOrderEvent(String rawPayload) {
        try {
            log.info("📩 Nyers bejövő üzenet: {}", rawPayload);
            String jsonBody = extractJsonPayload(rawPayload);

            OrderCreatedEvent event = objectMapper.readValue(jsonBody, OrderCreatedEvent.class);
            notificationService.processOrderNotification(event);

        } catch (Exception e) {
            log.error("❌ Hiba az üzenet feldolgozása közben! Az üzenet visszakerül a sorba/DLQ-ba.", e);
            throw new RuntimeException("Sikertelen üzenetfeldolgozás", e);
        }
    }

    private String extractJsonPayload(String rawPayload) throws Exception {
        if (rawPayload.contains("\"Type\"") && rawPayload.contains("\"Notification\"")) {
            JsonNode root = objectMapper.readTree(rawPayload);
            return root.get("Message").asText();
        }
        return rawPayload;
    }
}