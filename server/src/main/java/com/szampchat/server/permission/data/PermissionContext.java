package com.szampchat.server.permission.data;

/**
 * Specifies which id from endpoint path to use for authorization.
 * E.g.:
 * COMMUNITY requires communityId variable in path
 */
public enum PermissionContext {
    COMMUNITY,
    CHANNEL
}
