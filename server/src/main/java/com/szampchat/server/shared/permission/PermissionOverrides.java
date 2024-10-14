package com.szampchat.server.shared.permission;

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
public class PermissionOverrides {
    private long permissionOverrideData;

    public int apply(Permissions basePermissions) {
        int newPermissions = basePermissions.getPermissionData();

        newPermissions |= (int) permissionOverrideData;
        newPermissions &= ~((int)(permissionOverrideData >> 32));

        return newPermissions;
    }

    public Permissions applyToNew(Permissions basePermissions) {
        return new Permissions(apply(basePermissions));
    }
}
