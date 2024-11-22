package com.szampchat.server.shared.rabbitmq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("spring.rabbitmq")
@Getter
@Setter
public class RabbitMqConnectionProperties {
    private String host = "localhost";
    private int port = 5672;
    private String username = "root";
    private String password = "root";
}
