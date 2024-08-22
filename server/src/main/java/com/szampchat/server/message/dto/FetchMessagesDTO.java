package com.szampchat.server.message.dto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

@Data
public class FetchMessagesDTO {
    @Parameter(description = "Get messages before this message Snowflake ID", example = "21388369783160832")
    private Long before;
    @Parameter(description = "Max number of messages", example = "10")
    private Integer limit;
}
