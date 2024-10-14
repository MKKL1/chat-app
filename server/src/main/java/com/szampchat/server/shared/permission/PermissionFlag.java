package com.szampchat.server.shared.permission;

import lombok.Getter;

@Getter
public enum PermissionFlag {
    ROLE_MODIFY(0),
    INVITE_CREATE(1),
    CHANNEL_CREATE(2),
    CHANNEL_MODIFY(3),
    MESSAGE_CREATE(4),
    MESSAGE_DELETE(5),
    REACTION_CREATE(6);

    final byte offset;
    PermissionFlag(byte offset) {
        this.offset = offset;
    }

    PermissionFlag(int offset) {
        this.offset = (byte) offset;
    }
}
