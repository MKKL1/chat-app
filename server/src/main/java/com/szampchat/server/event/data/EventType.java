package com.szampchat.server.event.data;

/**
 * Enum representing the type of events that client can subscribe to.
 * <p>
 * For example type {@link EventType#MESSAGES} means that client will receive these events when they subscribe to {@code /community/{}/messages}
 */
public enum EventType {
    MESSAGES,
    GENERAL,
    NOTIFICATIONS
}
