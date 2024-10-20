package com.szampchat.server.permission.converters;

import com.szampchat.server.permission.data.Permissions;
import org.springframework.core.convert.converter.Converter;

public class IntToPermissionConverter implements Converter<Integer, Permissions> {

    @Override
    public Permissions convert(Integer source) {
        return new Permissions(source);
    }
}
