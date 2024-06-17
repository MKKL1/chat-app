package com.szampchat.server.snowflake;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Statement;

@Component
public class SnowflakeGenerator implements IdentifierGenerator {

    public static final String GENERATOR_NAME = "snowflakeIdGenerator";

    private final Snowflake snowflake;

    public SnowflakeGenerator(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return snowflake.nextId();
    }
}
