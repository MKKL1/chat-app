package com.szampchat.server.socket;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;

@Configuration
public class RabbitMqReceiverConfig {
    @Bean
    //Injecting ConnectionFactory from other module, that maybe will be separated
    public Receiver rabbitMqReceiver(ConnectionFactory connectionFactory) {
        return RabbitFlux.createReceiver(
                new ReceiverOptions()
                        .connectionFactory(connectionFactory)
                        .connectionSubscriptionScheduler(Schedulers.boundedElastic())
        );
    }
}
