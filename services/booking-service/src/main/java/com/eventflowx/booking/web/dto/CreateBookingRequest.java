package com.eventflowx.booking.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBookingRequest(
        @NotBlank(message = "eventId is required") String eventId,
        @NotBlank(message = "eventName is required") String eventName,
        @NotBlank(message = "userId is required") String userId) {
}
