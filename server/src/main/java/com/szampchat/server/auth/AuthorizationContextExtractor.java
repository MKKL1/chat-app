package com.szampchat.server.auth;

import org.springframework.security.web.server.authorization.AuthorizationContext;

public class AuthorizationContextExtractor {

    public static Long getChannelId(AuthorizationContext context) {
        return get(context, "channelId");
    }

    public static Long getCommunityId(AuthorizationContext context) {
        return get(context, "communityId");
    }

    public static Long getRoleId(AuthorizationContext context) {
        return get(context, "roleId");
    }

    private static Long get(AuthorizationContext context, String variable) {
        Object varObject = context.getVariables().get(variable);

        if(varObject == null)
            throw new NullPointerException("Path variable '" + variable + "' not found");

        if(!(varObject instanceof String varStr))
            throw new IllegalArgumentException(variable + " not instance of String");

        return Long.parseLong(varStr.trim());
    }
}
