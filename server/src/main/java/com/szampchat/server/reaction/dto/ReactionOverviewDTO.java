package com.szampchat.server.reaction.dto;

import lombok.Data;

@Data
public class ReactionOverviewDTO {
    private String emoji;
    private Integer count;
    private Boolean me;
}
