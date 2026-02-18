package com.eventflowx.wallet.messaging;

import com.eventflowx.wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BookingCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(BookingCreatedListener.class);

    private final ObjectMapper objectMapper;
    private final WalletService walletService;

    public BookingCreatedListener(ObjectMapper objectMapper, WalletService walletService) {
        this.objectMapper = objectMapper;
        this.walletService = walletService;
    }

    @RabbitListener(queues = "wallet.booking.created.queue")
    public void handle(String payload) {
        try {
            BookingCreatedEvent event = objectMapper.readValue(payload, BookingCreatedEvent.class);
            walletService.processBookingCreated(event);
        } catch (Exception e) {
            log.error("Failed to process booking event payload={}", payload, e);
            throw new RuntimeException("Failed to process booking event", e);
        }
    }
}
