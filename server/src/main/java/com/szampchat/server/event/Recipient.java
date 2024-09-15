package com.szampchat.server.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

//Recipient could be implemented to represent sets of recipients, like for example multiple communities or users.
// Not sure how it would be useful, but this implementation is open to change

/**
 * Represents message channel which receivers (clients) can subscribe to.
 * For example: recipient could be singular community,
 * when message with this recipient is published, it can be received by all users in this community.
 */
@Getter
@AllArgsConstructor
@Builder
public class Recipient {
    private final long id;
    private final Context context;
    private final Type type;

    public enum Context {
        COMMUNITY,
        USER,
        CHANNEL
    }

    public enum Type {
        MESSAGES
    }
}
