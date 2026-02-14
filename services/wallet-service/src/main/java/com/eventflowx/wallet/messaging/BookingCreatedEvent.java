package com.eventflowx.wallet.messaging;

public class BookingCreatedEvent {

    private String bookingId;
    private String userId;
    private String eventId;
    private String timestamp;

    public BookingCreatedEvent() {}

    public String getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
    public String getTimestamp() { return timestamp; }
}
