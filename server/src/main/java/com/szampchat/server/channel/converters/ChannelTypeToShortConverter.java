package com.szampchat.server.channel.converters;

import com.szampchat.server.channel.ChannelType;
import org.springframework.core.convert.converter.Converter;

public class ChannelTypeToShortConverter implements Converter<ChannelType, Short> {

    @Override
    public Short convert(ChannelType source) {
        return source.getValue();
    }
}
