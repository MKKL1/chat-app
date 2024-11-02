package com.szampchat.server.voice.exception;

public class InvalidParticipantException extends RuntimeException {
    public InvalidParticipantException(String participantId) {
        super("Invalid participantId (" + participantId + "). Participant is not managed by this app");
    }
}
