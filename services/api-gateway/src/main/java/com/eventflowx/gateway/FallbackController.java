package com.eventflowx.gateway;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/bookings")
    public ResponseEntity<Map<String, String>> bookingFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Booking service temporarily unavailable"));
    }

    @RequestMapping("/fallback/wallets")
    public ResponseEntity<Map<String, String>> walletFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Wallet service temporarily unavailable"));
    }

    @RequestMapping("/fallback/events")
    public ResponseEntity<Map<String, String>> eventFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Event service temporarily unavailable"));
    }
}
