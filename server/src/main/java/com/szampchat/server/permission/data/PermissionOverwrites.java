package com.szampchat.server.permission.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Stores allow and deny flags as 64-bit numbers.
 * <p>
 * Least significant bits are allow flags, while most significant bits are deny flags.
 * <p>
 * Simplified example:
 * If base permission is 0b0110 and permission override is 0b01001000,
 * then after #apply, permission will be 0b1010.
 */
/*
To illustrate:
Base:   0 1  0 1  0 1
Allow:  1 1  0 0  0 0
Deny:   0 0  1 1  0 0
Result: 1 1  0 0  0 1
 */
@Getter
@AllArgsConstructor
public class PermissionOverwrites {
    private static final int channelOverwriteMask = PermissionFlag.getChannelOverwriteMask();
    private long permissionOverwriteData;

    public PermissionOverwrites() {
        this.permissionOverwriteData = 0;
    }

    //TODO define context in which overwrite should be applied,
    //For example, invite_create should be community only flag, and it shouldn't be overridden by channel permissions
    public void allow(PermissionContext permissionContext, PermissionFlag permissionFlag) {
        allow(permissionContext, 1 << permissionFlag.getOffset());
    }

    public void allow(PermissionContext permissionContext, PermissionFlag... permissionFlags) {
        int combinedMask = 0;
        for (PermissionFlag flag : permissionFlags) {
            combinedMask |= (1 << flag.getOffset());
        }

        allow(permissionContext, combinedMask);
    }

    public void allow(PermissionContext permissionContext, int permissionMask) {
        int mask = permissionContext == PermissionContext.CHANNEL ? channelOverwriteMask : Integer.MAX_VALUE;
        permissionOverwriteData |= (permissionMask & mask);
    }

    public void deny(PermissionContext permissionContext, PermissionFlag permissionFlag) {
        deny(permissionContext, 1 << permissionFlag.getOffset());
    }

    public void deny(PermissionContext permissionContext, PermissionFlag... permissionFlags) {
        int combinedMask = 0;
        for (PermissionFlag flag : permissionFlags) {
            combinedMask |= (1 << flag.getOffset());
        }

        deny(permissionContext, combinedMask);
    }

    public void deny(PermissionContext permissionContext, int permissionMask) {
        int mask = permissionContext == PermissionContext.CHANNEL ? channelOverwriteMask : Integer.MAX_VALUE;
        permissionOverwriteData |= (long) (permissionMask & mask) << 32;
    }

    public int apply(Permissions basePermissions) {
        int newPermissions = basePermissions.getPermissionData();

        newPermissions |= (int) permissionOverwriteData;
        newPermissions &= ~((int)(permissionOverwriteData >> 32));

        return newPermissions;
    }

    public Permissions applyToNew(Permissions basePermissions) {
        return new Permissions(apply(basePermissions));
    }
}
