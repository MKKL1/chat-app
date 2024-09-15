package com.szampchat.server.event;

import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
@Getter
public class RabbitMqConfig {
    @Bean
    public Sender rabbitMqSender() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root");

        return RabbitFlux.createSender(
                new SenderOptions()
                        .connectionFactory(connectionFactory)
        );
    }
}
