package com.szampchat.server.reaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionListDTO {
    private Collection<ReactionUsersDTO> reactions;
}
