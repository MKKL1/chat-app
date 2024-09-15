package com.szampchat.server.event;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.rabbitmq")
@Getter
public class RabbitMqProperties {
    private final String chatExchange = "chat.exchange";
    private final String messageExchange = "chat.exchange.messages";
}
