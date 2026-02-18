package com.eventflowx.booking.events;

import com.eventflowx.common.events.OutboxEvent;
import com.eventflowx.common.domain.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RetryTemplate rabbitRetryTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishEvents() {

        List<OutboxEvent> events =
                outboxRepository.findTop20ByProcessedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {
            try {
                rabbitRetryTemplate.execute(context -> {
                    rabbitTemplate.convertAndSend(
                            "booking.exchange",
                            "booking.created",
                            event.getPayload()
                    );
                    return null;
                });

                event.markProcessed();
                log.info("Published outbox event");

            } catch (Exception e) {
                event.incrementRetry();
                log.warn("Failed publishing outbox event. retryCount incremented", e);

                if (!event.canRetry()) {
                    event.markProcessed(); // avoid infinite retry
                }
            }
        }
    }
}
