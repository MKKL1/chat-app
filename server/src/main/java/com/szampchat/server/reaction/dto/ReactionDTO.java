package com.szampchat.server.reaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
public class ReactionDTO {
    @Schema(example = "\uD83D\uDC4D", description = "Emoji in unicode")
    private String emoji;
//    @Schema(example = "10", description = "How many users used this reaction")
    private Set<Long> users;
}
