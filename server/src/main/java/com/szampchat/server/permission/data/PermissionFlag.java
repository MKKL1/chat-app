package com.szampchat.server.permission.data;

import lombok.Getter;

//TODO It doesn't have to be enum
@Getter
public enum PermissionFlag {
    ADMINISTRATOR(0, false),
    ROLE_MODIFY(1, false),
    INVITE_CREATE(2, false),
    CHANNEL_CREATE(3, true),
    CHANNEL_MODIFY(4, true),
    MESSAGE_CREATE(5, true),
    MESSAGE_DELETE(6, true),
    REACTION_CREATE(7, true);

    final byte offset;
    final boolean channelOverwrite;

    PermissionFlag(int offset, boolean channel) {
        this.offset = (byte) offset;
        this.channelOverwrite = channel;
    }

    public int asMask() {
        return 1 << offset;
    }

    /**
     * @return 1 - channel can overwrite this permission
     */
    public static int getChannelOverwriteMask() {
        int mask = 0;
        for (PermissionFlag permissionFlag : PermissionFlag.values()) {
            if(permissionFlag.channelOverwrite)
                mask |= 1 << permissionFlag.offset;
        }
        return mask;
    }
}
