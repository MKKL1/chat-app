package com.szampchat.server.user;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private String description;
}
