package com.szampchat.server.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.server.ResponseStatusException;

public class AuthorizationContextExtractor {

    public static long getChannelId(AuthorizationContext context) {
        return get(context, "channelId");
    }

    public static long getCommunityId(AuthorizationContext context) {
        return get(context, "communityId");
    }

    public static long getRoleId(AuthorizationContext context) {
        return get(context, "roleId");
    }

    private static long get(AuthorizationContext context, String variable) {
        Object varObject = context.getVariables().get(variable);

        if(varObject == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing " + variable);

        if(!(varObject instanceof String varStr))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + variable);

        long longId;
        try {
            longId = Long.parseLong(varStr.trim());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid " + variable);
        }
        return longId;
    }
}
