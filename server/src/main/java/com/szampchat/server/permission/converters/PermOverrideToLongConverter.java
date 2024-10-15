package com.szampchat.server.permission.converters;

import com.szampchat.server.permission.data.PermissionOverrides;
import org.springframework.core.convert.converter.Converter;

public class PermOverrideToLongConverter implements Converter<PermissionOverrides, Long> {
    @Override
    public Long convert(PermissionOverrides source) {
        return source.getPermissionOverrideData();
    }
}
