package com.szampchat.server.reaction.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmojiUserPair {
    private String emoji;
    private Long userId;
}
