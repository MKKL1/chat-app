package com.szampchat.server.permission.converters;

import com.szampchat.server.permission.data.PermissionOverwrites;
import org.springframework.core.convert.converter.Converter;

public class PermOverrideToLongConverter implements Converter<PermissionOverwrites, Long> {
    @Override
    public Long convert(PermissionOverwrites source) {
        return source.getPermissionOverwriteData();
    }
}
