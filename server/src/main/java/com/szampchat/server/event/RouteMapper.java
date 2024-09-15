package com.szampchat.server.event;

import org.springframework.stereotype.Component;

import java.util.EnumMap;

@Component
public class RouteMapper {
    private final EnumMap<Recipient.Context, String> recipientToContextMap = new EnumMap<>(Recipient.Context.class);
    private final EnumMap<Recipient.Type, String> recipientToTypeMap = new EnumMap<>(Recipient.Type.class);

    public RouteMapper() {
        recipientToContextMap.put(Recipient.Context.USER, "user.");
        recipientToContextMap.put(Recipient.Context.COMMUNITY, "community.");
        recipientToContextMap.put(Recipient.Context.CHANNEL, "channel.");

        recipientToTypeMap.put(Recipient.Type.MESSAGES, "messages.");
    }

    public String toRoute(Recipient recipient) {
        return recipientToTypeMap.get(recipient.getType()) + recipientToContextMap.get(recipient.getContext()) + recipient.getId();
    }
}
