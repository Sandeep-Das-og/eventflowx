package com.eventflowx.wallet.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventflowx.wallet.domain.ProcessedEventRepository;
import com.eventflowx.wallet.domain.Wallet;
import com.eventflowx.wallet.domain.WalletRepository;
import com.eventflowx.wallet.messaging.BookingCreatedEvent;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletService(walletRepository, processedEventRepository, 1000, 50);
    }

    @Test
    void processBookingCreated_skipsDuplicateEvent() {
        BookingCreatedEvent event = new BookingCreatedEvent();
        event.setEventId("evt-1");
        event.setBookingId("booking-1");
        event.setUserId("user-1");

        when(processedEventRepository.existsById("evt-1")).thenReturn(true);

        walletService.processBookingCreated(event);

        verify(walletRepository, never()).save(org.mockito.ArgumentMatchers.any(Wallet.class));
    }

    @Test
    void processBookingCreated_registersEventWithoutDebitingWallet() {
        BookingCreatedEvent event = new BookingCreatedEvent();
        event.setEventId("evt-1");
        event.setBookingId("booking-1");
        event.setUserId("user-1");

        when(processedEventRepository.existsById("evt-1")).thenReturn(false);
        when(walletRepository.findById("user-1")).thenReturn(Optional.of(new Wallet("user-1", 1000)));

        walletService.processBookingCreated(event);

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(processedEventRepository).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void credit_addsBalance() {
        when(walletRepository.findById("user-2")).thenReturn(Optional.of(new Wallet("user-2", 200)));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet wallet = walletService.credit("user-2", 25);

        assertThat(wallet.getBalance()).isEqualTo(225);
    }

    @Test
    void chargePayment_debitsWallet() {
        when(walletRepository.findById("user-3")).thenReturn(Optional.of(new Wallet("user-3", 300)));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = walletService.chargePayment("booking-3", "user-3", "RAZORPAY", "UPI", 75);

        assertThat(response.get("status")).isEqualTo("SUCCESS");
        assertThat(response.get("remainingBalance")).isEqualTo(225.0);
        verify(walletRepository).save(org.mockito.ArgumentMatchers.argThat(wallet ->
                wallet.getUserId().equals("user-3") && wallet.getBalance() == 225));
    }
}
