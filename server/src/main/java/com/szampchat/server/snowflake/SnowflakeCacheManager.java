package com.szampchat.server.snowflake;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SnowflakeCacheManager<E> {
    //TODO remove not used snowflake's from map (for now it will work, but will fill memory in long run)
    private final Map<E, Snowflake> snowflakeCache = new ConcurrentHashMap<>();

    public long getNextId(E context) {
        Snowflake snowflakeGenerator = snowflakeCache.get(context);
        if(snowflakeGenerator == null) {
            snowflakeGenerator = new Snowflake(SnowflakeConfiguration.NODE_ID, SnowflakeConfiguration.CUSTOM_EPOCH);
            snowflakeCache.put(context, snowflakeGenerator);
        }
        return snowflakeGenerator.nextId();
    }
}
