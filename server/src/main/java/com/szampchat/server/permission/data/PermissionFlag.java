package com.szampchat.server.permission.data;

import lombok.Getter;

@Getter
public enum PermissionFlag {
    ADMINISTRATOR(0),
    ROLE_MODIFY(1),
    INVITE_CREATE(2),
    CHANNEL_CREATE(3),
    CHANNEL_MODIFY(4),
    MESSAGE_CREATE(5),
    MESSAGE_DELETE(6),
    REACTION_CREATE(7);

    final byte offset;
    PermissionFlag(byte offset) {
        this.offset = offset;
    }

    PermissionFlag(int offset) {
        this.offset = (byte) offset;
    }
}
