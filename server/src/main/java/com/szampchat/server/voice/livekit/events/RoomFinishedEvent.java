package com.szampchat.server.voice.livekit.events;

import livekit.LivekitModels;
import livekit.LivekitWebhook;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoomFinishedEvent implements LivekitEvent {
    private final LivekitWebhook.WebhookEvent event;
    private final LivekitModels.Room room;

    public static RoomFinishedEvent create(LivekitWebhook.WebhookEvent event) {
        return new RoomFinishedEvent(event, event.getRoom());
    }
}
