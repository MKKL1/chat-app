package com.szampchat.server.reaction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionUpdateRequest {
    //TODO validate
    private String emoji;
}
