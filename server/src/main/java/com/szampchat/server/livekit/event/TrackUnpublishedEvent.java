package com.szampchat.server.livekit.event;

import livekit.LivekitModels;
import livekit.LivekitWebhook;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TrackUnpublishedEvent implements LivekitEvent {
    private final LivekitWebhook.WebhookEvent event;
    private final LivekitModels.Room room;
    private final LivekitModels.ParticipantInfo participantInfo;
    private final LivekitModels.TrackInfo trackInfo;

    public static TrackUnpublishedEvent create(LivekitWebhook.WebhookEvent event) {
        return new TrackUnpublishedEvent(event, event.getRoom(), event.getParticipant(), event.getTrack());
    }
}
