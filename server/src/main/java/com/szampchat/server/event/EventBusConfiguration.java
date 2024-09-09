package com.szampchat.server.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;

@Configuration
public class EventBusConfiguration {

    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }

}
