package com.szampchat.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonSerializationContext;
import com.nimbusds.jose.shaded.gson.JsonSerializer;
import com.szampchat.server.channel.ChannelType;

import java.io.IOException;
import java.lang.reflect.Type;

public class ChannelTypeSerializer extends StdSerializer<ChannelType> {

    public ChannelTypeSerializer() {
        super(ChannelType.class);
    }

    @Override
    public void serialize(ChannelType channelType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(channelType.getValue());
    }
}
