package com.eventflowx.booking.events;

import com.eventflowx.common.events.BaseEvent;

public class BookingCreatedEvent extends BaseEvent {

    private final String bookingId;
    private final String userId;
    private final String eventId;

    public BookingCreatedEvent(String correlationId,
                               String bookingId,
                               String userId,
                               String eventId) {
        super(correlationId);
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventId = eventId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public String getEventId() {
        return eventId;
    }
}
