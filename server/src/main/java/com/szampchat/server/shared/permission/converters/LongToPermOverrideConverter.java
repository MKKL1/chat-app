package com.szampchat.server.shared.permission.converters;

import com.szampchat.server.shared.permission.PermissionOverrides;
import org.springframework.core.convert.converter.Converter;

public class LongToPermOverrideConverter implements Converter<Long, PermissionOverrides> {
    @Override
    public PermissionOverrides convert(Long source) {
        return new PermissionOverrides(source);
    }
}
