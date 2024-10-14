package com.szampchat.server.channel.converters;

import com.szampchat.server.channel.ChannelType;
import org.springframework.core.convert.converter.Converter;

public class ChannelTypeToByteConverter implements Converter<ChannelType, Byte> {

    @Override
    public Byte convert(ChannelType source) {
        return source.getValue();
    }
}
