package com.szampchat.server.community.exception;

import org.springframework.security.core.AuthenticationException;

//TODO to be removed
public class NotCommunityMemberException extends AuthenticationException {

    public NotCommunityMemberException() {
        super("User is not a member of community");
    }
}
