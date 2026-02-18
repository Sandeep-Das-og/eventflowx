package com.eventflowx.booking.web;

import com.eventflowx.booking.service.BookingService;
import com.eventflowx.booking.web.dto.CreateBookingRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, String>> create(@Valid @RequestBody CreateBookingRequest request) {
        String bookingId = bookingService.createBooking(request.customerName(), request.eventName());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("bookingId", bookingId));
    }
}
