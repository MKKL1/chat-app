package com.szampchat.server.community;

import com.szampchat.server.user.UserDTO;
import com.szampchat.server.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
public class CommunityMemberDTO {
    private UserDTO user;
    private Set<Long> roles;
}
