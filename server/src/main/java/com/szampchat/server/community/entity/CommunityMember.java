package com.szampchat.server.community.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("community_members")
public class CommunityMember {
    @Column("community_id")
    private Long communityId;

    @Column("user_id")
    private Long userId;
}
