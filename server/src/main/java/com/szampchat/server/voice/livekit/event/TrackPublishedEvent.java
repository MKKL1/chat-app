package com.szampchat.server.voice.livekit.event;

import livekit.LivekitModels;
import livekit.LivekitWebhook;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TrackPublishedEvent implements LivekitEvent {
    private final LivekitWebhook.WebhookEvent event;
    private final LivekitModels.Room room;
    private final LivekitModels.ParticipantInfo participantInfo;
    private final LivekitModels.TrackInfo trackInfo;

    public static TrackPublishedEvent create(LivekitWebhook.WebhookEvent event) {
        return new TrackPublishedEvent(event, event.getRoom(), event.getParticipant(), event.getTrack());
    }
}
