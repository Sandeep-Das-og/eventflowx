package com.eventflowx.booking.service;

import com.eventflowx.booking.domain.Booking;
import com.eventflowx.booking.domain.BookingRepository;
import com.eventflowx.booking.events.BookingCreatedEvent;
import com.eventflowx.common.events.OutboxEvent;
import com.eventflowx.common.domain.OutboxRepository;
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
    public String createBooking(String customerName, String eventName) throws Exception {

        // 1️⃣ Save booking
        Booking booking = new Booking(customerName, eventName);
        bookingRepository.save(booking);

        // 2️⃣ Create domain event
        BookingCreatedEvent event = new BookingCreatedEvent(
                "corr-" + booking.getId(),
                booking.getId(),
                customerName,
                eventName
        );

        // 3️⃣ Store in outbox
        OutboxEvent outbox = new OutboxEvent();
        outbox.setEventType(event.getClass().getSimpleName());
        outbox.setPayload(objectMapper.writeValueAsString(event));
        outbox.setPublished(false);

        outboxRepository.save(outbox);

        return booking.getId();
    }
}
