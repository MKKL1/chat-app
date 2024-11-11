package com.szampchat.server.reaction.exception;

import com.szampchat.server.shared.exception.NotFoundException;
import org.springframework.http.HttpStatus;

public class ReactionNotFoundException extends NotFoundException {
    public ReactionNotFoundException(String emoji) {
        super("reaction", emoji);
    }
}
