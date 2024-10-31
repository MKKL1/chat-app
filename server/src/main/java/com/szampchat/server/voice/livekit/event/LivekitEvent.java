package com.szampchat.server.voice.livekit.event;

import livekit.LivekitWebhook;

public interface LivekitEvent {
    LivekitWebhook.WebhookEvent getEvent();
}
