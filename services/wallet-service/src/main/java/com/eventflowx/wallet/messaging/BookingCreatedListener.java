package com.eventflowx.wallet.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BookingCreatedListener {

    private final ObjectMapper objectMapper;

    public BookingCreatedListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "wallet.booking.created.queue")
    public void handle(String payload) {

        try {
            BookingCreatedEvent event =
                    objectMapper.readValue(payload, BookingCreatedEvent.class);

            System.out.println("Processing booking: " + event.getBookingId());

            // TODO: call wallet service logic here

        } catch (Exception e) {
            throw new RuntimeException("Failed to process booking event", e);
        }
    }
}
