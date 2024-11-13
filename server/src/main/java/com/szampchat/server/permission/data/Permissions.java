package com.szampchat.server.permission.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
@Schema(implementation = Integer.class)
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
        return (permissionData & (1 << permissionFlag.data.offset())) != 0;
    }

    public boolean has(int permissionMask) {
        return (permissionData & permissionMask) == permissionMask;
    }

    public boolean has(PermissionFlag... permissionFlags) {
        int combinedMask = 0;
        for (PermissionFlag flag : permissionFlags) {
            combinedMask |= (1 << flag.data.offset());
        }

        return has(combinedMask);
    }

    public void allow(PermissionFlag permissionFlag) {
        permissionData |= (1 << permissionFlag.data.offset());
    }

    public void allow(PermissionFlag... permissionFlags) {
        allow(PermissionFlag.combineFlags(permissionFlags));
    }

    public void allow(int permissionMask) {
        permissionData |= permissionMask;
    }

    public void deny(PermissionFlag permissionFlag) {
        permissionData &= ~(1 << permissionFlag.data.offset());
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

    @Override
    public String toString() {
        if(permissionData == Integer.MAX_VALUE) return "Permissions: ALL";
        return "Permissions: " + Arrays.stream(PermissionFlag.values())
                .filter(this::has)
                .map(PermissionFlag::toString)
                .toList();
    }
}
