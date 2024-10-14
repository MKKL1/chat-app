package com.szampchat.server.channel.converters;

import com.szampchat.server.channel.ChannelType;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ByteToChannelTypeConverter implements Converter<Byte, ChannelType> {
    Map<Byte, ChannelType> byteChannelTypeMap = new HashMap<>();
    public ByteToChannelTypeConverter() {
        for(ChannelType channelType : ChannelType.values()) {
            byteChannelTypeMap.put(channelType.getValue(), channelType);
        }
    }

    @Override
    public ChannelType convert(Byte source) {
        return byteChannelTypeMap.get(source);
    }
}
