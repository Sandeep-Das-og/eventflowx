package com.eventflowx.booking.web;

import com.eventflowx.booking.service.BookingService;
import com.eventflowx.booking.web.dto.CreateBookingRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_booking.write', 'ROLE_admin')")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateBookingRequest request) {
        String bookingId = bookingService.createBooking(request.userId(), request.eventId(), request.eventName());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "bookingId", bookingId,
                "paymentRequired", true,
                "eventId", request.eventId(),
                "userId", request.userId()
        ));
    }

    @GetMapping("/analytics/events/{eventId}")
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public ResponseEntity<Map<String, Object>> eventAnalytics(@PathVariable String eventId) {
        long count = bookingService.countByEventId(eventId);
        return ResponseEntity.ok(Map.of("eventId", eventId, "bookingsCount", count));
    }
}
