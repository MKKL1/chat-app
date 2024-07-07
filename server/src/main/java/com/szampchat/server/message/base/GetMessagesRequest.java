package com.szampchat.server.message.base;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Optional;

@Data
public class GetMessagesRequest {
    @Parameter(description = "Get messages before this message Snowflake ID", example = "21388369783160832")
    private Long before;
    @Parameter(description = "Max number of messages", example = "10")
    private Integer limit;
}
