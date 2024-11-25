package com.szampchat.server.shared.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;
import reactor.util.retry.Retry;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

@Slf4j
@Configuration
@Getter
public class RabbitMqConfiguration {
    private final static long maxRetries = 10;
    private final static Duration backoffDuration = Duration.ofSeconds(2);

    @Bean
    public ConnectionFactory connectionFactoryRabbitMq(RabbitMqConnectionProperties properties) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        connectionFactory.setHost(properties.getHost());
        connectionFactory.setPort(properties.getPort());
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());

        log.info("RabbitMQ on: {}:{}", properties.getHost(), properties.getPort());

        return connectionFactory;
    }

    @Bean
    public Sender rabbitMqSender(ConnectionFactory connectionFactory) {
        return RabbitFlux.createSender(
                        new SenderOptions()
                                .connectionFactory(connectionFactory)
                                .connectionMonoConfigurator(
                                        cm -> cm.retryWhen(RetrySpec.backoff(maxRetries, backoffDuration)
                                                .doBeforeRetry(retrySignal ->
                                                        log.warn("Retrying sender connection to RabbitMQ due to error: {}", retrySignal.failure().getMessage())
                                                )
                                                .onRetryExhaustedThrow((_, rs) -> rs.failure())
                                        )
                                )
                );
    }

    @Bean
    public Receiver rabbitMqReceiver(ConnectionFactory connectionFactory) {
        return RabbitFlux.createReceiver(
                        new ReceiverOptions()
                                .connectionFactory(connectionFactory)
                                .connectionSubscriptionScheduler(Schedulers.boundedElastic())
                                .connectionMonoConfigurator(
                                        cm -> cm.retryWhen(RetrySpec.backoff(maxRetries, backoffDuration)
                                                .doBeforeRetry(retrySignal ->
                                                        log.warn("Retrying receiver connection to RabbitMQ due to error: {}", retrySignal.failure().getMessage())
                                                )
                                                .onRetryExhaustedThrow((_, rs) -> rs.failure())
                                        )
                                )
        );
    }
}
