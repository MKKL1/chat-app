package com.szampchat.server.channel.converters;

import com.szampchat.server.channel.ChannelType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.HashMap;
import java.util.Map;

@ReadingConverter
public class ShortToChannelTypeConverter implements Converter<Short, ChannelType> {
    Map<Short, ChannelType> byteChannelTypeMap = new HashMap<>();
    public ShortToChannelTypeConverter() {
        for(ChannelType channelType : ChannelType.values()) {
            byteChannelTypeMap.put(channelType.getValue(), channelType);
        }
    }

    @Override
    public ChannelType convert(Short source) {
        return byteChannelTypeMap.get(source);
    }
}
