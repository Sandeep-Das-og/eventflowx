package com.eventflowx.wallet.service;

import com.eventflowx.wallet.domain.ProcessedEvent;
import com.eventflowx.wallet.domain.ProcessedEventRepository;
import com.eventflowx.wallet.domain.Wallet;
import com.eventflowx.wallet.domain.WalletRepository;
import com.eventflowx.wallet.messaging.BookingCreatedEvent;
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

        Wallet wallet = walletRepository.findById(event.getUserId())
                .orElseGet(() -> new Wallet(event.getUserId(), defaultBalance));

        wallet.debit(bookingCharge);
        walletRepository.save(wallet);
        processedEventRepository.save(new ProcessedEvent(event.getEventId()));

        log.info("Processed booking event eventId={} bookingId={} userId={} debited={}",
                event.getEventId(), event.getBookingId(), event.getUserId(), bookingCharge);
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
}
