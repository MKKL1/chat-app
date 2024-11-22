package com.szampchat.server.livekit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.livekit")
@Getter
@Setter
@NoArgsConstructor
public class LiveKitProperties {
    private String url = "http://localhost:7880";
    private String key = "devkey";
    private String secret = "secret";
}