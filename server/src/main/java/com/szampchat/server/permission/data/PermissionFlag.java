package com.szampchat.server.permission.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PermissionFlag {
    ADMINISTRATOR(new PermissionFlagData(0, false, false, "ADMINISTRATOR")),
    INVITE_CREATE(new PermissionFlagData(2, false, false, "INVITE_CREATE")),
    CHANNEL_CREATE(new PermissionFlagData(3, false, false, "CHANNEL_CREATE")),

    CHANNEL_MODIFY(new PermissionFlagData(4, true, false, "CHANNEL_MODIFY")),
    MESSAGE_CREATE(new PermissionFlagData(5, true, true, "MESSAGE_CREATE")),
    MESSAGE_DELETE(new PermissionFlagData(6, true, true, "MESSAGE_DELETE")),
    REACTION_CREATE(new PermissionFlagData(7, true, true, "REACTION_CREATE"));

    final PermissionFlagData data;

    public int asMask() {
        return 1 << data.offset();
    }

    /**
     * @return 1 - channel can overwrite this permission
     */
    public static int getChannelOverwriteMask() {
        int mask = 0;
        for (PermissionFlag permissionFlag : PermissionFlag.values()) {
            if(permissionFlag.data.canChannelOverwrite())
                mask |= 1 << permissionFlag.data.offset();
        }
        return mask;
    }

    public static int getDefaultPermissionMask() {
        int mask = 0;
        for (PermissionFlag permissionFlag : PermissionFlag.values()) {
            if(permissionFlag.data.isDefault())
                mask |= 1 << permissionFlag.data.offset();
        }
        return mask;
    }

    public static int combineFlags(PermissionFlag... permissionFlags) {
        int mask = 0;
        for (PermissionFlag permissionFlag : permissionFlags) {
            mask |= 1 << permissionFlag.data.offset();
        }
        return mask;
    }

    @Override
    public String toString() {
        return data.name();
    }
}
