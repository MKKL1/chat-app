package com.szampchat.server.permission.converters;

import com.szampchat.server.permission.data.PermissionOverwrites;
import org.springframework.core.convert.converter.Converter;

public class LongToPermOverrideConverter implements Converter<Long, PermissionOverwrites> {
    @Override
    public PermissionOverwrites convert(Long source) {
        return new PermissionOverwrites(source);
    }
}
