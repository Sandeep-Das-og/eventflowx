package com.eventflowx.booking.service;

import com.eventflowx.booking.domain.Booking;
import com.eventflowx.booking.domain.BookingRepository;
import com.eventflowx.booking.events.BookingCreatedEvent;
import com.eventflowx.common.domain.OutboxRepository;
import com.eventflowx.common.events.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public BookingService(
            BookingRepository bookingRepository,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper) {
        this.bookingRepository = bookingRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public String createBooking(String userId, String eventId, String eventName) {

        Booking booking = bookingRepository.save(new Booking(userId, eventId, eventName));

        BookingCreatedEvent event = new BookingCreatedEvent(
                "corr-" + booking.getId(),
                booking.getId(),
                userId,
                eventId
        );

        try {
            OutboxEvent outbox = new OutboxEvent(
                    "Booking",
                    booking.getId(),
                    event.getClass().getSimpleName(),
                    objectMapper.writeValueAsString(event)
            );
            outboxRepository.save(outbox);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to persist outbox event", e);
        }

        return booking.getId();
    }

    @Transactional(readOnly = true)
    public long countByEventId(String eventId) {
        return bookingRepository.countByEventId(eventId);
    }
}
