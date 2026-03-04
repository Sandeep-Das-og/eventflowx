package com.eventflowx.wallet.web;

import com.eventflowx.wallet.domain.Wallet;
import com.eventflowx.wallet.service.WalletService;
import com.eventflowx.wallet.web.dto.ChargePaymentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/wallets/{userId}")
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public ResponseEntity<?> getWallet(@PathVariable String userId) {
        return walletService.getWallet(userId)
                .<ResponseEntity<?>>map(wallet -> ResponseEntity.ok(Map.of(
                        "userId", wallet.getUserId(),
                        "balance", wallet.getBalance())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Wallet not found")));
    }

    @PostMapping("/wallets/{userId}/credit")
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public ResponseEntity<Map<String, Object>> credit(
            @PathVariable String userId,
            @RequestParam @DecimalMin(value = "0.01") double amount) {

        Wallet wallet = walletService.credit(userId, amount);
        return ResponseEntity.ok(Map.of("userId", wallet.getUserId(), "balance", wallet.getBalance()));
    }

    @PostMapping("/payments/charge")
    @PreAuthorize("hasAnyAuthority('ROLE_wallet.credit', 'ROLE_admin')")
    public ResponseEntity<Map<String, Object>> charge(@Valid @RequestBody ChargePaymentRequest request) {
        return ResponseEntity.ok(walletService.chargePayment(
                request.bookingId(),
                request.userId(),
                request.paymentGateway(),
                request.paymentMethodType(),
                request.amount()
        ));
    }
}
