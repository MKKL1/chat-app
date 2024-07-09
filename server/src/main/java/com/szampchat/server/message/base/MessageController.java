package com.szampchat.server.message.base;

import com.szampchat.server.message.base.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class MessageController {
    private final MessageService messageService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Get messages for given channel")
    @GetMapping("/channels/{channelId}/messages")
    public Flux<MessageMergedDTO> getMessages(@Parameter(description = "Snowflake ID of text channel", example = "20276884193411072")
                                         @PathVariable Long channelId,
                                     @ParameterObject GetMessagesRequest getMessagesRequest) {
        return messageService.getMessages(channelId, getMessagesRequest)
                .map(messageMergedData -> modelMapper.map(messageMergedData, MessageMergedDTO.class));
    }
}
