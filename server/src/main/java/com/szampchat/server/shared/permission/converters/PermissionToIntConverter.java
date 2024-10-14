package com.szampchat.server.shared.permission.converters;

import com.szampchat.server.shared.permission.Permissions;
import org.springframework.core.convert.converter.Converter;

public class PermissionToIntConverter implements Converter<Permissions, Integer> {
    @Override
    public Integer convert(Permissions source) {
        return source.getPermissionData();
    }
}
