package com.eventflowx.wallet.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.eventflowx.wallet.domain.ProcessedEventRepository;
import com.eventflowx.wallet.domain.WalletRepository;
import com.eventflowx.wallet.messaging.BookingCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class WalletServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("wallet_db")
            .withUsername("wallet_user")
            .withPassword("wallet_password");

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management");

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.profiles.active", () -> "dev");
        registry.add("eventflowx.security.enabled", () -> "false");
    }

    @Test
    void processBookingCreated_isIdempotent() {
        BookingCreatedEvent event = new BookingCreatedEvent();
        event.setEventId("evt-123");
        event.setBookingId("booking-123");
        event.setUserId("user-123");

        walletService.processBookingCreated(event);
        walletService.processBookingCreated(event);

        var wallet = walletRepository.findById("user-123").orElseThrow();
        assertThat(wallet.getBalance()).isEqualTo(1000);
        assertThat(processedEventRepository.count()).isEqualTo(1);
    }
}
