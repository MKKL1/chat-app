package com.szampchat.server.event;

public interface SendEvent<T> {
    String getName();
    Recipient getRecipient();
    T getData();
}
