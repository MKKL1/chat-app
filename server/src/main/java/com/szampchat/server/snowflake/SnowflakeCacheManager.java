package com.szampchat.server.snowflake;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SnowflakeCacheManager<E> {
    //TODO remove not used snowflake's from map (for now it will work, but will fill memory in long run)
    private final Map<E, SnowflakeGen> snowflakeCache = new ConcurrentHashMap<>();

    public long getNextId(E context) {
        SnowflakeGen snowflakeGenGenerator = snowflakeCache.get(context);
        if(snowflakeGenGenerator == null) {
            snowflakeGenGenerator = new SnowflakeGen(SnowflakeConfiguration.NODE_ID, SnowflakeConfiguration.CUSTOM_EPOCH);
            snowflakeCache.put(context, snowflakeGenGenerator);
        }
        return snowflakeGenGenerator.nextId();
    }
}
