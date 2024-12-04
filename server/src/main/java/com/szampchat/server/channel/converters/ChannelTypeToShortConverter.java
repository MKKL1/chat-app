package com.szampchat.server.channel.converters;

import com.szampchat.server.channel.entity.ChannelType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class ChannelTypeToShortConverter implements Converter<ChannelType, Short> {

    @Override
    public Short convert(ChannelType source) {
        return source.getValue();
    }
}
