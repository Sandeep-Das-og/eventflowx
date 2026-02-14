package com.eventflowx.booking.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String customerName;
    private String eventName;
    private Instant createdAt = Instant.now();

    protected Booking() {}

    public Booking(String customerName, String eventName) {
        this.customerName = customerName;
        this.eventName = eventName;
    }

    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getEventName() { return eventName; }
}
