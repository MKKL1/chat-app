package com.szampchat.server.snowflake;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.lang.StringTemplate.STR;

@Slf4j
@SpringBootTest
public class SnowflakeTests {

    @Autowired
    private Snowflake snowflake;

    @Test
    public void test_generation() {
        //TODO add tests
        log.info(STR."Generated snowflake \{snowflake.nextId()}");
        log.info(STR."Snowflake \{snowflake.toString()}" );
    }
}
