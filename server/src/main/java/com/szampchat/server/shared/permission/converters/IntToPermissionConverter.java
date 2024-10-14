package com.szampchat.server.shared.permission.converters;

import com.szampchat.server.shared.permission.Permissions;
import org.springframework.core.convert.converter.Converter;

public class IntToPermissionConverter implements Converter<Integer, Permissions> {

    @Override
    public Permissions convert(Integer source) {
        return new Permissions(source);
    }
}
