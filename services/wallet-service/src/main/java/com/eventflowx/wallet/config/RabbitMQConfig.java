package com.eventflowx.wallet.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange("booking.exchange");
    }

    @Bean
    public Queue walletQueue() {
        return new Queue("wallet.booking.created.queue", true);
    }

    @Bean
    public Binding walletBinding(Queue walletQueue,
                                 TopicExchange bookingExchange) {
        return BindingBuilder
                .bind(walletQueue)
                .to(bookingExchange)
                .with("booking.created");
    }
}
