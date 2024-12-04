package com.szampchat.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.szampchat.server.permission.data.PermissionOverwrites;

import java.io.IOException;

public class PermissionOverwritesSerializer extends StdSerializer<PermissionOverwrites> {

    public PermissionOverwritesSerializer() {
        super(PermissionOverwrites.class);
    }

    @Override
    public void serialize(PermissionOverwrites permissionOverwrites, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(permissionOverwrites.getPermissionOverwriteData());
    }
}
