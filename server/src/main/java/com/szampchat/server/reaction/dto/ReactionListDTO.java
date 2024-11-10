package com.szampchat.server.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

//Used for serialization with redis, as I couldn't figure out how to save plain list in json
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionListDTO {
    private Collection<ReactionCountDTO> reactions;
}
