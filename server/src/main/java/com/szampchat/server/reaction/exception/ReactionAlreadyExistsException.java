package com.szampchat.server.reaction.exception;

import com.szampchat.server.shared.exception.AlreadyExistsException;

public class ReactionAlreadyExistsException extends AlreadyExistsException {
    public ReactionAlreadyExistsException(String emoji) {
        super("reaction", "Reaction with emoji " + emoji);
    }
}
