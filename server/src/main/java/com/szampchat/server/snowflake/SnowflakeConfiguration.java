package com.szampchat.server.snowflake;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class SnowflakeConfiguration {
    public static final int NODE_ID = 0;
    public static final long CUSTOM_EPOCH = 1714867200000L; //White2115 urodziny

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SnowflakeGen snowflake() {
        return new SnowflakeGen(NODE_ID, CUSTOM_EPOCH);
    }
}
