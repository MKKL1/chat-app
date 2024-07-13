package com.szampchat.server.message.entity;

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
