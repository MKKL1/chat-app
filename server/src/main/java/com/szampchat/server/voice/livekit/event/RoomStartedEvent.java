package com.szampchat.server.voice.livekit.event;

import livekit.LivekitModels;
import livekit.LivekitWebhook;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoomStartedEvent implements LivekitEvent {
    private final LivekitWebhook.WebhookEvent event;
    private final LivekitModels.Room room;

    public static RoomStartedEvent create(LivekitWebhook.WebhookEvent event) {
        return new RoomStartedEvent(event, event.getRoom());
    }
}
