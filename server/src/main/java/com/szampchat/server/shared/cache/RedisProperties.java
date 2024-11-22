package com.szampchat.server.shared.cache;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.redis")
@Getter
@Setter
@NoArgsConstructor
public class RedisProperties {
    private String host = "localhost";
    private int port = 6379;
}