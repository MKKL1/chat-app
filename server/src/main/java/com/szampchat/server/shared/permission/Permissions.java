package com.szampchat.server.shared.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Permissions {
    private int permissionData;

    public boolean has(PermissionFlag permissionFlag) {
        return (permissionData | (1 << permissionFlag.getOffset())) != 0;
    }

    public boolean has(int permissionMask) {
        return (permissionData & permissionMask) == permissionMask;
    }

    public boolean has(PermissionFlag... permissionFlags) {
        int combinedMask = 0;
        for (PermissionFlag flag : permissionFlags) {
            combinedMask |= (1 << flag.getOffset());
        }

        return has(combinedMask);
    }

    public void set(PermissionFlag permissionFlag) {
        permissionData |= (1 << permissionFlag.getOffset());
    }

    public void set(int permissionMask) {
        permissionData |= permissionMask;
    }

    public void set(PermissionFlag... permissionFlags) {
        int combinedMask = 0;
        for (PermissionFlag flag : permissionFlags) {
            combinedMask |= (1 << flag.getOffset());
        }
        set(combinedMask);
    }
}
