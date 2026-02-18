package com.eventflowx.common.events;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    private String id;

    private String aggregateType;
    private String aggregateId;
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private boolean processed;
    private int retryCount;

    private LocalDateTime createdAt;

    public OutboxEvent() {}

    public OutboxEvent(String aggregateType,
                       String aggregateId,
                       String eventType,
                       String payload) {
        this.id = UUID.randomUUID().toString();
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.processed = false;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    // setters

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void markProcessed() {
        this.processed = true;
    }

    public void incrementRetry() {
        this.retryCount++;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public boolean canRetry() {
        return retryCount < 5;
    }

    public String getPayload() {
        return payload;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
