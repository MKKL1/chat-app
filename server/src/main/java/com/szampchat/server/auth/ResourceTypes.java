package com.szampchat.server.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResourceTypes {
    COMMUNITY("COMMUNITY"),
    CHANNEL("CHANNEL"),
    ROLE("ROLE"),
    USER("USER");

    private final String typeId;
}
