package com.szampchat.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.szampchat.server.channel.ChannelType;
import com.szampchat.server.permission.data.Permissions;

import java.io.IOException;

public class PermissionSerializer extends StdSerializer<Permissions> {

    public PermissionSerializer() {
        super(Permissions.class);
    }

    @Override
    public void serialize(Permissions permissions, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(permissions.getPermissionData());
    }
}
