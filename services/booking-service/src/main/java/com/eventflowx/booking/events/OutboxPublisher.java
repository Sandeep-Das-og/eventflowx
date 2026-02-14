package com.eventflowx.booking.events;

import com.eventflowx.common.events.OutboxEvent;
import com.eventflowx.common.domain.OutboxRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;

    public OutboxPublisher(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publish() {

        List<OutboxEvent> events = outboxRepository.findByPublishedFalse();

        for (OutboxEvent event : events) {

            // 🔥 Simulate publishing
            System.out.println("Publishing event: " + event.getPayload());

            event.setPublished(true);
            outboxRepository.save(event);
        }
    }
}
