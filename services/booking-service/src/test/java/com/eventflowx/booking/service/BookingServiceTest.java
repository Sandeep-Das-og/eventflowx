package com.eventflowx.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eventflowx.booking.domain.Booking;
import com.eventflowx.booking.domain.BookingRepository;
import com.eventflowx.common.domain.OutboxRepository;
import com.eventflowx.common.events.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_savesBookingAndOutbox() throws Exception {
        Booking booking = new Booking("u1", "e1", "concert");
        var idField = Booking.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(booking, "booking-1");

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"event\":\"BOOKING_CREATED\"}");

        String result = bookingService.createBooking("u1", "e1", "concert");

        assertThat(result).isEqualTo("booking-1");
        verify(bookingRepository).save(any(Booking.class));

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxRepository).save(outboxCaptor.capture());
        assertThat(outboxCaptor.getValue().getPayload()).contains("BOOKING_CREATED");
    }
}
