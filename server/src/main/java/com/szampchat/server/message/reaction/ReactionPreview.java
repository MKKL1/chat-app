package com.szampchat.server.message.reaction;

import lombok.Data;

//TODO make name of this class more initiative of it's meaning
@Data
public class ReactionPreview {
    private String emoji;
    private Integer count;
    private Boolean me;
}
