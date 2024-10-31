package com.szampchat.server.voice.livekit.events;

import livekit.LivekitWebhook;

public interface LivekitEvent {
    LivekitWebhook.WebhookEvent getEvent();
}
