package com.szampchat.server.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionUsersBulkDTO {
    private Long messageId;
    private String emoji;
    private Set<Long> users;
}
