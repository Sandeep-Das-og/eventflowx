package com.eventflowx.booking.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class BookingEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public BookingEventPublisher(RabbitTemplate rabbitTemplate,
                                 ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishBookingCreated(String bookingId,
                                      String userId,
                                      String eventId) {

        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "BOOKING_CREATED");
        event.put("bookingId", bookingId);
        event.put("userId", userId);
        event.put("eventId", eventId);
        event.put("timestamp", Instant.now().toString());

        try {
            String payload = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(
                    "booking.exchange",
                    "booking.created",
                    payload
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to publish booking event", e);
        }
    }
}
