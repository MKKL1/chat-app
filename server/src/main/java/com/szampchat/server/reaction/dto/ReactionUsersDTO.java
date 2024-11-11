package com.szampchat.server.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionUsersDTO {
//    @Schema(example = "\uD83D\uDC4D", description = "Emoji in unicode")
    private String emoji;
//    @Schema(example = "10", description = "How many users used this reaction")
    private Set<Long> users;
}
