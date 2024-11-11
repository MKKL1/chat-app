package com.szampchat.server.shared.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szampchat.server.reaction.entity.ReactionList;
import io.lettuce.core.codec.RedisCodec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class JsonCodec<T> implements RedisCodec<String, T> {
    private final ObjectMapper objectMapper;
    private final Class<T> clazz;

    public JsonCodec(ObjectMapper objectMapper, Class<T> clazz) {
        this.objectMapper = objectMapper;
        this.clazz = clazz;
    }

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return StandardCharsets.UTF_8.decode(bytes).toString();
    }

    @Override
    public T decodeValue(ByteBuffer bytes) {
        String json = StandardCharsets.UTF_8.decode(bytes).toString();
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to decode JSON", e);
        }
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return StandardCharsets.UTF_8.encode(key);
    }

    @Override
    public ByteBuffer encodeValue(T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            return StandardCharsets.UTF_8.encode(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to encode to JSON", e);
        }
    }
}
