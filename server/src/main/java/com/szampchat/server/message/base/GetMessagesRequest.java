package com.szampchat.server.message.base;

import lombok.Data;

import java.util.Optional;

@Data
public class GetMessagesRequest {
    private Long before;
    private Integer limit;
}
