package com.eventflowx.event.domain;

import java.time.Instant;

public class Event {

    private final String id;
    private final String name;
    private final String city;
    private final double price;
    private final Instant createdAt;

    public Event(String id, String name, String city, double price, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.price = price;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public double getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
