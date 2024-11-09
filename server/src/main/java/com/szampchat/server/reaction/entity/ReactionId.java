package com.szampchat.server.reaction.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class ReactionId implements Serializable {
    private Long id;
    private Long channelId;
}
