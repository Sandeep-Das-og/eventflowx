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

    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
