package com.szampchat.server.permission.converters;

import com.szampchat.server.permission.data.PermissionOverrides;
import org.springframework.core.convert.converter.Converter;

public class LongToPermOverrideConverter implements Converter<Long, PermissionOverrides> {
    @Override
    public PermissionOverrides convert(Long source) {
        return new PermissionOverrides(source);
    }
}
