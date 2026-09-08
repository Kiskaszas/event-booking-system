package hu.viktor.orderservice.scheduler;

import hu.viktor.orderservice.model.OutboxEvent;
import hu.viktor.orderservice.repository.OutboxEventRepository;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelayService {

    private final OutboxEventRepository repository;
    private final SnsTemplate snsTemplate;

    @Value("${order.sns.topic-arn}")
    private String topicArn;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> pendingEvents = repository.findByProcessedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : pendingEvents) {
            try {
                snsTemplate.convertAndSend(topicArn, event.getPayload());

                event.setProcessed(true);
                log.info("Outbox esemény sikeresen publikálva: {}", event.getId());
            } catch (Exception e) {
                log.error("Hiba az outbox esemény publikálása során: {}", event.getId(), e);
            }
        }
    }
}