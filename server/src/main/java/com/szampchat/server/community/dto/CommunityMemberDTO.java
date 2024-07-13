package com.szampchat.server.community.dto;

import com.szampchat.server.user.dto.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class CommunityMemberDTO {
    private final UserDTO user;
    private final Set<Long> roles;
}
