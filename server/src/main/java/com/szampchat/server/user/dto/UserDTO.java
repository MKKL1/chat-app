package com.szampchat.server.user.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private String description;
}
