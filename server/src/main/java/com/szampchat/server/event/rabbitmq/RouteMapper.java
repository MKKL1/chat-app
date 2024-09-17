package com.szampchat.server.event.rabbitmq;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import org.springframework.stereotype.Component;

import java.util.EnumMap;

/**
 * A component responsible for mapping event types and recipients to specific route key strings used in rabbitmq.
 * <p>
 * Example:
 * If an event is of type {@code MESSAGES} and the recipient is {@code COMMUNITY},
 * the route generated route would follow the pattern "messages.community.{recipientId}".
 *
 * <p>
 * Usage:
 * <pre>
 * {@code new reactor.rabbitmq.OutboundMessage(exchange, routeMapper.toRouteKey(event), data)}
 * </pre>
 */
@Component
public class RouteMapper {
    private final EnumMap<Recipient.Context, String> recipientToContextMap = new EnumMap<>(Recipient.Context.class);
    private final EnumMap<EventType, String> recipientToTypeMap = new EnumMap<>(EventType.class);

    public RouteMapper() {
        recipientToContextMap.put(Recipient.Context.USER, "user.");
        recipientToContextMap.put(Recipient.Context.COMMUNITY, "community.");
        recipientToContextMap.put(Recipient.Context.CHANNEL, "channel.");

        recipientToTypeMap.put(EventType.MESSAGES, "messages.");
    }

    public String toRouteKey(InternalEvent<?> event) {
        return toRouteKey(event.getType(), event.getRecipient());
    }

    public String toRouteKey(EventType eventType, Recipient recipient) {
        return recipientToTypeMap.get(eventType) + recipientToContextMap.get(recipient.getContext()) + recipient.getId();
    }
}
