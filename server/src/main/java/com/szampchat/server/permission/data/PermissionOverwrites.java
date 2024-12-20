package com.szampchat.server.permission.data;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(implementation = Long.class)
public class PermissionOverwrites {
    private static final int channelOverwriteMask = PermissionFlag.getChannelOverwriteMask();
    private long permissionOverwriteData;

    public PermissionOverwrites() {
        this.permissionOverwriteData = 0;
    }

    //TODO define context in which overwrite should be applied,
    //For example, invite_create should be community only flag, and it shouldn't be overridden by channel permissions
    public void allow(PermissionScope permissionScope, PermissionFlag permissionFlag) {
        allow(permissionScope, 1 << permissionFlag.data.offset());
    }

    public void allow(PermissionScope permissionScope, PermissionFlag... permissionFlags) {
        int combinedMask = 0;
        for (PermissionFlag flag : permissionFlags) {
            combinedMask |= (1 << flag.data.offset());
        }

        allow(permissionScope, combinedMask);
    }

    public void allow(PermissionScope permissionScope, int permissionMask) {
        int mask = permissionScope == PermissionScope.CHANNEL ? channelOverwriteMask : Integer.MAX_VALUE;
        permissionOverwriteData |= (permissionMask & mask);
    }

    public void deny(PermissionScope permissionScope, PermissionFlag permissionFlag) {
        deny(permissionScope, 1 << permissionFlag.data.offset());
    }

    public void deny(PermissionScope permissionScope, PermissionFlag... permissionFlags) {
        int combinedMask = 0;
        for (PermissionFlag flag : permissionFlags) {
            combinedMask |= (1 << flag.data.offset());
        }

        deny(permissionScope, combinedMask);
    }

    public void deny(PermissionScope permissionScope, int permissionMask) {
        int mask = permissionScope == PermissionScope.CHANNEL ? channelOverwriteMask : Integer.MAX_VALUE;
        permissionOverwriteData |= (long) (permissionMask & mask) << 32;
    }

    public int apply(Permissions basePermissions, PermissionScope scope) {
        int newPermissions = basePermissions.getPermissionData();

        long validatedOverwriteData = validateAndGet(scope);
        newPermissions |= (int) validatedOverwriteData;
        newPermissions &= ~((int)(validatedOverwriteData >> 32));

        return newPermissions;
    }

    public Permissions applyToNew(Permissions basePermissions, PermissionScope scope) {
        return new Permissions(apply(basePermissions, scope));
    }

    public long sum(PermissionOverwrites other) {
        return permissionOverwriteData | other.getPermissionOverwriteData();
    }

    public long sum(long other) {
        return permissionOverwriteData | other;
    }

    public long validateAndGet(PermissionScope scope) {
        int mask = scope == PermissionScope.CHANNEL ? channelOverwriteMask : Integer.MAX_VALUE;
        return permissionOverwriteData & mask;
    }
}
