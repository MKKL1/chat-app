package com.szampchat.server.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("user_subject")
public class UserSubject {
    @Column("user_id")
    private Long userId;
    @Column("sub")
    private UUID sub;
}
