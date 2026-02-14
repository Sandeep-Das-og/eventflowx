package com.eventflowx.booking.web;

import com.eventflowx.booking.service.BookingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public String create(
            @RequestParam String customerName,
            @RequestParam String eventName) throws Exception {

        return bookingService.createBooking(customerName, eventName);
    }
}
