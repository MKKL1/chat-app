package com.szampchat.server.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReactionOverviewDTO {
    private String emoji;
    private Integer count;
    private Boolean me;
}
