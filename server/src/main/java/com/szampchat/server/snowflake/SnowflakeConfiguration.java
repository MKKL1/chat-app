package com.szampchat.server.snowflake;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeConfiguration {
    public static final int NODE_ID = 0;
    public static final long CUSTOM_EPOCH = 1714867200000L; //White2115 urodziny

    @Bean
    public Snowflake snowflake() {
        return new Snowflake(NODE_ID, CUSTOM_EPOCH);
    }
}
