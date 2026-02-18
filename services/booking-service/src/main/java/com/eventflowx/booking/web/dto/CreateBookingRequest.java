package com.eventflowx.booking.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBookingRequest(
        @NotBlank(message = "customerName is required") String customerName,
        @NotBlank(message = "eventName is required") String eventName) {
}
