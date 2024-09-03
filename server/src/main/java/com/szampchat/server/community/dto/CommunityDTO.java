package com.szampchat.server.community.dto;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.user.entity.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommunityDTO {
    Community community;
    User owner;

    public CommunityDTO(User u, Community c) {
    }
}
