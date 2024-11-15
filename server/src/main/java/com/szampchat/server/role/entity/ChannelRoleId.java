package com.szampchat.server.role.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelRoleId implements Serializable {
    @Id
    private Long roleId;
    @Id
    private Long channelId;
}
