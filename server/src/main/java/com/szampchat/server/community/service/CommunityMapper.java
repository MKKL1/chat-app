package com.szampchat.server.community.service;

import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.entity.Community;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CommunityMapper {
    private final ModelMapper modelMapper;
    public CommunityDTO toDTO(Community community) {
        return modelMapper.map(community, CommunityDTO.class);
    }
}
