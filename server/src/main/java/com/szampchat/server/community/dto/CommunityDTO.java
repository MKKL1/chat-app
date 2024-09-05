package com.szampchat.server.community.dto;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CommunityDTO {
    private final Community community;
    private final User owner;
}
