package com.szampchat.server.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

//TODO make name of this class more initiative of it's meaning
@Data
public class ReactionPreviewDTO {
    @Schema(example = "\uD83D\uDC4D", description = "Emoji in unicode")
    private String emoji;
    @Schema(example = "10", description = "How many users used this reaction")
    private Integer count;
    @Schema(example = "true", description = "If current user reacted to the message")
    private Boolean me;
}
