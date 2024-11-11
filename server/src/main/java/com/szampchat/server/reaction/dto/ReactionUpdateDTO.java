package com.szampchat.server.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReactionUpdateDTO {
    private String emoji;
    private Long channelId;
    private Long messageId;
    private Long userId;
}
