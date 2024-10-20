package com.szampchat.server.permission.converters;

import com.szampchat.server.permission.data.Permissions;
import org.springframework.core.convert.converter.Converter;

public class PermissionToIntConverter implements Converter<Permissions, Integer> {
    @Override
    public Integer convert(Permissions source) {
        return source.getPermissionData();
    }
}
