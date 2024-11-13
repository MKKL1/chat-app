package com.szampchat.server.permission;

import com.szampchat.server.permission.data.ResourceTypes;
import reactor.core.publisher.Mono;

public interface ResourceAccessHandler {
    ResourceTypes getType();
    Mono<Boolean> hasAccess(long resourceId, long userId);
}
