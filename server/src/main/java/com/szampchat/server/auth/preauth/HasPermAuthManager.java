package com.szampchat.server.auth.preauth;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.authorization.ReactiveAuthorizationManager;

public interface HasPermAuthManager extends ReactiveAuthorizationManager<MethodInvocation> {

}
