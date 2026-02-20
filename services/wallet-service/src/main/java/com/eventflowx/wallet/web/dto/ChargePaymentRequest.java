package com.eventflowx.wallet.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record ChargePaymentRequest(
        @NotBlank(message = "bookingId is required") String bookingId,
        @NotBlank(message = "userId is required") String userId,
        @NotBlank(message = "paymentGateway is required") String paymentGateway,
        @NotBlank(message = "paymentMethodType is required") String paymentMethodType,
        @DecimalMin(value = "0.01", message = "amount must be > 0") double amount) {
}
