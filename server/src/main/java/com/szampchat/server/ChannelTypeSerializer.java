package com.szampchat.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.szampchat.server.channel.entity.ChannelType;

import java.io.IOException;

public class ChannelTypeSerializer extends StdSerializer<ChannelType> {

    public ChannelTypeSerializer() {
        super(ChannelType.class);
    }

    @Override
    public void serialize(ChannelType channelType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(channelType.getValue());
    }
}
