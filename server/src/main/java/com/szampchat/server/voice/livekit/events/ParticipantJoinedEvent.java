package com.szampchat.server.voice.livekit.events;

import livekit.LivekitModels;
import livekit.LivekitWebhook;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ParticipantJoinedEvent implements LivekitEvent {
    private final LivekitWebhook.WebhookEvent event;
    private final LivekitModels.Room room;
    private final LivekitModels.ParticipantInfo participantInfo;

    public static ParticipantJoinedEvent create(LivekitWebhook.WebhookEvent event) {
        return new ParticipantJoinedEvent(event, event.getRoom(), event.getParticipant());
    }
}
