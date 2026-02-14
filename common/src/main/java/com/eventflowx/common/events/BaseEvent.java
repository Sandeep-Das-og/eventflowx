package com.eventflowx.common.events;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseEvent {

    private final String eventId;
    private final Instant timestamp;
    private final String correlationId;

    protected BaseEvent(String correlationId) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.correlationId = correlationId;
    }

    public String getEventId() {
        return eventId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
