package com.eventflowx.wallet.service;

import com.eventflowx.wallet.domain.ProcessedEvent;
import com.eventflowx.wallet.domain.ProcessedEventRepository;
import com.eventflowx.wallet.domain.Wallet;
import com.eventflowx.wallet.domain.WalletRepository;
import com.eventflowx.wallet.messaging.BookingCreatedEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final double defaultBalance;
    private final double bookingCharge;
    private static final List<String> SUPPORTED_METHODS = List.of("UPI", "CREDIT_CARD", "DEBIT_CARD");

    public WalletService(
            WalletRepository walletRepository,
            ProcessedEventRepository processedEventRepository,
            @Value("${wallet.default-balance:1000}") double defaultBalance,
            @Value("${wallet.booking-charge:50}") double bookingCharge) {
        this.walletRepository = walletRepository;
        this.processedEventRepository = processedEventRepository;
        this.defaultBalance = defaultBalance;
        this.bookingCharge = bookingCharge;
    }

    @Transactional
    public void processBookingCreated(BookingCreatedEvent event) {
        if (processedEventRepository.existsById(event.getEventId())) {
            log.info("Skipping duplicate booking event eventId={} bookingId={}", event.getEventId(), event.getBookingId());
            return;
        }

        walletRepository.findById(event.getUserId())
                .orElseGet(() -> walletRepository.save(new Wallet(event.getUserId(), defaultBalance)));

        processedEventRepository.save(new ProcessedEvent(event.getEventId()));

        log.info("Registered booking event eventId={} bookingId={} userId={} (payment pending)",
                event.getEventId(), event.getBookingId(), event.getUserId());
    }

    @Transactional(readOnly = true)
    public Optional<Wallet> getWallet(String userId) {
        return walletRepository.findById(userId);
    }

    @Transactional
    public Wallet credit(String userId, double amount) {
        Wallet wallet = walletRepository.findById(userId)
                .orElseGet(() -> new Wallet(userId, defaultBalance));
        wallet.credit(amount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Map<String, Object> chargePayment(
            String bookingId,
            String userId,
            String paymentGateway,
            String paymentMethodType,
            double amount) {
        validateGatewayAndMethod(paymentGateway, paymentMethodType);

        Wallet wallet = walletRepository.findById(userId)
                .orElseGet(() -> new Wallet(userId, defaultBalance));

        wallet.debit(amount);
        Wallet saved = walletRepository.save(wallet);

        return Map.of(
                "bookingId", bookingId,
                "userId", userId,
                "paymentGateway", paymentGateway,
                "paymentMethodType", paymentMethodType,
                "status", "SUCCESS",
                "chargedAmount", amount,
                "remainingBalance", saved.getBalance()
        );
    }

    private void validateGatewayAndMethod(String paymentGateway, String paymentMethodType) {
        if (!SUPPORTED_METHODS.contains(paymentMethodType)) {
            throw new IllegalArgumentException("Unsupported paymentMethodType: " + paymentMethodType);
        }

        boolean supported = switch (paymentGateway) {
            case "RAZORPAY" -> true;
            case "STRIPE", "PAYPAL" -> !"UPI".equals(paymentMethodType);
            default -> false;
        };

        if (!supported) {
            throw new IllegalArgumentException("Unsupported gateway/method combination");
        }
    }
}
