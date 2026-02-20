package com.eventflowx.event.service;

import com.eventflowx.event.domain.Event;
import com.eventflowx.event.web.dto.CreateEventRequest;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EventService {

    private final Map<String, Event> events = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate;
    private final String bookingServiceBaseUrl;

    public EventService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${booking.service.base-url:http://booking-service:8080}") String bookingServiceBaseUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.bookingServiceBaseUrl = bookingServiceBaseUrl;

        seedDefaultEvents();
    }

    public Event create(CreateEventRequest request) {
        Event event = new Event(
                UUID.randomUUID().toString(),
                request.name(),
                request.city(),
                request.price(),
                Instant.now()
        );
        events.put(event.getId(), event);
        return event;
    }

    public List<Event> listAll() {
        return events.values().stream()
                .sorted(Comparator.comparing(Event::getCreatedAt).reversed())
                .toList();
    }

    public Event findById(String eventId) {
        Event event = events.get(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        return event;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> analytics(String eventId) {
        Event event = findById(eventId);
        String url = bookingServiceBaseUrl + "/bookings/analytics/events/" + eventId;

        Map<String, Object> bookingStats = restTemplate.getForObject(url, Map.class);
        long bookingsCount = bookingStats == null ? 0L : ((Number) bookingStats.getOrDefault("bookingsCount", 0)).longValue();

        return Map.of(
                "eventId", event.getId(),
                "eventName", event.getName(),
                "city", event.getCity(),
                "price", event.getPrice(),
                "bookingsCount", bookingsCount,
                "createdAt", event.getCreatedAt().toString()
        );
    }

    private void seedDefaultEvents() {
        create(new CreateEventRequest("Music Concert", "Bengaluru", 49.99));
        create(new CreateEventRequest("Standup Night", "Hyderabad", 29.99));
        create(new CreateEventRequest("Tech Summit", "Pune", 99.00));
    }
}
