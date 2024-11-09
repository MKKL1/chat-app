package com.szampchat.server.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class ReactionOverviewBulkDTO {
    private Long messageId;
    private Collection<ReactionOverviewDTO> reactionOverviewDTOS;
}
