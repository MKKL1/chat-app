package com.szampchat.server.message.dto;

import lombok.Data;

//TODO make name of this class more initiative of it's meaning
@Data
public class ReactionPreviewDTO {
    private String emoji;
    private Integer count;
    private Boolean me;
}
