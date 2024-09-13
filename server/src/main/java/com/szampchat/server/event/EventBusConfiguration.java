package com.szampchat.server.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfiguration {

    @Bean
    public MessageEventBus messageEventBus() {
        return new MessageEventBus();
    }

}
