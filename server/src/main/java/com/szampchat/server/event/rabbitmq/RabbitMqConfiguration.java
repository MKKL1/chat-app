package com.szampchat.server.event.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
@Getter
public class RabbitMqConfiguration {

    @Bean
    public ConnectionFactory connectionFactoryRabbitMq(RabbitMqConnectionProperties properties) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        //Hardcoding it for now
        connectionFactory.setHost(properties.getHost());
        connectionFactory.setPort(properties.getPort());
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());
        return connectionFactory;
    }

    @Bean
    public Sender rabbitMqSender(ConnectionFactory connectionFactory) {
        //TODO log to console when connection was successful
        return RabbitFlux.createSender(
                new SenderOptions()
                        .connectionFactory(connectionFactory)
        );
    }
}
