package com.szampchat.server.livekit.event;

import livekit.LivekitWebhook;

public interface LivekitEvent {
    LivekitWebhook.WebhookEvent getEvent();
}
