package com.szampchat.server.permission.data;

public record PermissionFlagData(
        int offset,
        boolean canChannelOverwrite,
        boolean isDefault,
        String name
) { }
