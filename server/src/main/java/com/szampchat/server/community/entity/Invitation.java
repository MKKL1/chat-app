package com.szampchat.server.community.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("invitations")
public class Invitation {
    @Id()
    @Column("id")
    private Long id;

    @Column("community_id")
    private Long communityId;

    @Column("expired_at")
    private LocalDateTime expiredAt;

    public String toLink(){
        return STR."community/\{communityId}/join\{id}";
    }
}
