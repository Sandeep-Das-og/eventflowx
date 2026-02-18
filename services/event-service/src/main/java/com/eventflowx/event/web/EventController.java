package com.eventflowx.event.web;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {

    @GetMapping
    public List<Map<String, Object>> listEvents() {
        return List.of(
                Map.of("id", "evt-101", "name", "Music Concert", "city", "Bengaluru", "price", 49.99),
                Map.of("id", "evt-102", "name", "Standup Night", "city", "Hyderabad", "price", 29.99),
                Map.of("id", "evt-103", "name", "Tech Summit", "city", "Pune", "price", 99.0)
        );
    }
}
