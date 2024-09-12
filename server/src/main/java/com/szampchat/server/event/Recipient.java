package com.szampchat.server.event;

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
public class Recipient {
    private final long id;
    private final Context context;

    private Recipient(long id, Context context) {
        this.id = id;
        this.context = context;
    }

    public static Recipient fromCommunity(long communityId) {
        return new Recipient(communityId, Context.COMMUNITY);
    }

    /**
     * Recipient which represents singular user.
     * Could be used for personal changes/messages
     */
    public static Recipient fromUser(long userId) {
        return new Recipient(userId, Context.USER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipient recipient = (Recipient) o;
        return id == recipient.id && context == recipient.context;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, context);
    }

    public enum Context {
        COMMUNITY,
        USER
    }
}
