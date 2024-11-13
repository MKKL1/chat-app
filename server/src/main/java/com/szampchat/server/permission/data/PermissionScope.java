package com.szampchat.server.permission.data;

/**
 *
 */
public enum PermissionScope {
    COMMUNITY,
    CHANNEL,
    /**
     * A bit different from others. It is used to extract communityId from role, so it could be though as community scope
     */
    ROLE
}
