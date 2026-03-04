package com.eventflowx.event.web;

import com.eventflowx.event.domain.Event;
import com.eventflowx.event.service.EventService;
import com.eventflowx.event.web.dto.CreateEventRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    @PreAuthorize("hasAnyAuthority('ROLE_event.read', 'ROLE_admin')")
    public List<Event> listEvents() {
        return eventService.listAll();
    }

    @GetMapping("/events/{eventId}")
    @PreAuthorize("hasAnyAuthority('ROLE_event.read', 'ROLE_admin')")
    public Event eventById(@PathVariable String eventId) {
        return eventService.findById(eventId);
    }

    @PostMapping("/admin/events")
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public ResponseEntity<Event> createEvent(@Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    @GetMapping("/admin/events")
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public List<Event> listAdminEvents() {
        return eventService.listAll();
    }

    @GetMapping("/admin/events/{eventId}/analytics")
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public Map<String, Object> eventAnalytics(@PathVariable String eventId) {
        return eventService.analytics(eventId);
    }
}
