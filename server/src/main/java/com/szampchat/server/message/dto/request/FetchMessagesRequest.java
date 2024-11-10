package com.szampchat.server.message.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class FetchMessagesRequest {
    @Parameter(description = "Get messages before this message Snowflake ID", example = "21388369783160832")
    private Long before;
    @Parameter(description = "Max number of messages", example = "10")
    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 100, message = "Limit cannot exceed 100")
    private Integer limit;
    @Parameter(description = "List of messages to retrieve. Ignores other parameters if set")
    @Size(max = 10, message = "Cannot retrieve more than 10 messages at a time")
    private List<Long> messages;
}
