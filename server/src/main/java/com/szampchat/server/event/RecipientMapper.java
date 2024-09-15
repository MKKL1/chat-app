package com.szampchat.server.event;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.EnumMap;

@Component
public class RecipientMapper {
    private final EnumMap<Recipient.Context, String> recipientToRouteMap = new EnumMap<>(Recipient.Context.class);

    public RecipientMapper() {
        recipientToRouteMap.put(Recipient.Context.USER, "user.");
        recipientToRouteMap.put(Recipient.Context.COMMUNITY, "community.");
        recipientToRouteMap.put(Recipient.Context.CHANNEL, "channel.");
    }

    public String toRoute(Recipient recipient) {
        return recipientToRouteMap.get(recipient.getContext()) + recipient.getId();
    }
}
