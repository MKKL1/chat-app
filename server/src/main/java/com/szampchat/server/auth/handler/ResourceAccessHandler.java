package com.szampchat.server.auth.handler;

import com.szampchat.server.auth.ResourceTypes;
import reactor.core.publisher.Mono;

public interface ResourceAccessHandler {
    ResourceTypes getType();
    Mono<Boolean> hasAccess(long resourceId, long userId);
}
