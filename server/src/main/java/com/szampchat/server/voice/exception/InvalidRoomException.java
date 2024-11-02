package com.szampchat.server.voice.exception;

public class InvalidRoomException extends RuntimeException {
    public InvalidRoomException(String roomId) {
        super("Invalid roomId (" + roomId + "). Room is not managed by this app");
    }
}
