package com.szampchat.server.event;

import lombok.Getter;

@Getter
public class Recipient {
    private final long id;
    private final Context context;

    private Recipient(long id, Context context) {
        this.id = id;
        this.context = context;
    }

    public static Recipient toCommunity(long communityId) {
        return new Recipient(communityId, Context.COMMUNITY);
    }

    public static Recipient toUser(long userId) {
        return new Recipient(userId, Context.USER);
    }

    public enum Context {
        COMMUNITY,
        USER
    }
}
