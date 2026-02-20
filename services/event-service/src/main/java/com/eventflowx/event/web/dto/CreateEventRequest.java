package com.eventflowx.event.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record CreateEventRequest(
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "city is required") String city,
        @DecimalMin(value = "0.01", message = "price must be > 0") double price) {
}
