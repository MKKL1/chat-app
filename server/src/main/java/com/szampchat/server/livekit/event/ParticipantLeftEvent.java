package com.szampchat.server.livekit.event;

import livekit.LivekitModels;
import livekit.LivekitWebhook;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ParticipantLeftEvent implements LivekitEvent {
    private final LivekitWebhook.WebhookEvent event;
    private final LivekitModels.Room room;
    private final LivekitModels.ParticipantInfo participantInfo;

    public static ParticipantLeftEvent create(LivekitWebhook.WebhookEvent event) {
        return new ParticipantLeftEvent(event, event.getRoom(), event.getParticipant());
    }
}
