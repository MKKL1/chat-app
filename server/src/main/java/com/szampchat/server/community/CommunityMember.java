package com.szampchat.server.community;

import com.szampchat.server.user.UserDTO;
import com.szampchat.server.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class CommunityMember {
    private final User user;
    private final Set<Long> roles;
}
