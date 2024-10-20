package com.szampchat.server.permission.data;

import lombok.Getter;

//TODO It doesn't have to be enum
@Getter
public enum PermissionFlag {
    ADMINISTRATOR(0, false, false),
    ROLE_MODIFY(1, false, false),
    INVITE_CREATE(2, false, false),
    CHANNEL_CREATE(3, true, false),
    CHANNEL_MODIFY(4, true, false),
    MESSAGE_CREATE(5, true, true),
    MESSAGE_DELETE(6, true, true),
    REACTION_CREATE(7, true, true);

    final byte offset;
    final boolean channelOverwrite;
    final boolean defaultPerm;

    PermissionFlag(int offset, boolean channel, boolean defaultPerm) {
        this.offset = (byte) offset;
        this.channelOverwrite = channel;
        this.defaultPerm = defaultPerm;
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

    public static int getDefaultPermissionMask() {
        int mask = 0;
        for (PermissionFlag permissionFlag : PermissionFlag.values()) {
            if(permissionFlag.defaultPerm)
                mask |= 1 << permissionFlag.offset;
        }
        return mask;
    }

    public static int combineFlags(PermissionFlag... permissionFlags) {
        int mask = 0;
        for (PermissionFlag permissionFlag : permissionFlags) {
            mask |= 1 << permissionFlag.offset;
        }
        return mask;
    }
}
