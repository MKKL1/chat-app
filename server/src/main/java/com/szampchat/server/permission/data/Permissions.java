package com.szampchat.server.permission.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Permissions {
    private int permissionData;

    public Permissions() {
        permissionData = PermissionFlag.getDefaultPermissionMask();
    }

    public static Permissions allDenied() {
        return new Permissions(0);
    }

    public static Permissions allAllowed() {
        return new Permissions(Integer.MAX_VALUE);
    }

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

    public void allow(PermissionFlag permissionFlag) {
        permissionData |= (1 << permissionFlag.getOffset());
    }

    public void allow(PermissionFlag... permissionFlags) {
        allow(PermissionFlag.combineFlags(permissionFlags));
    }

    public void allow(int permissionMask) {
        permissionData |= permissionMask;
    }

    public void deny(PermissionFlag permissionFlag) {
        permissionData &= ~(1 << permissionFlag.getOffset());
    }

    public void deny(PermissionFlag... permissionFlags) {
        deny(PermissionFlag.combineFlags(permissionFlags));
    }

    public void deny(int permissionMask) {
        permissionData &= ~permissionMask;
    }

    public void allowAll() {
        permissionData = Integer.MAX_VALUE;
    }

    public void denyAll() {
        permissionData = 0;
    }
}
