package com.szampchat.server.permission.data;

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
