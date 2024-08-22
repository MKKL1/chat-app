package com.szampchat.server.message;

import com.szampchat.server.message.dto.FetchMessagesDTO;
import com.szampchat.server.message.dto.MessageCreateDTO;
import com.szampchat.server.message.dto.MessageDTO;
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
public class MessageController {
    private final MessageService messageService;
    private final ModelMapper modelMapper;

    @Operation(summary = "Get messages for given channel")
    @GetMapping("/channels/{channelId}/messages")
    public Flux<MessageDTO> getMessages(@Parameter(description = "Snowflake ID of text channel", example = "20276884193411072")
                                         @PathVariable Long channelId,
                                        @ParameterObject FetchMessagesDTO fetchMessagesDTO) {
        return messageService.getMessages(channelId, fetchMessagesDTO)
                .map(messageMergedData -> modelMapper.map(messageMergedData, MessageDTO.class));
    }

    @PostMapping("/channels/{channelId}/messages")
    public Mono<MessageDTO> createMessage(@PathVariable Long channelId, MessageCreateDTO messageCreateDTO) {
        return Mono.empty();
    }

    @PatchMapping("/channels/{channelId}/messages/{messageId}")
    public Mono<MessageDTO> editMessage(@PathVariable Long channelId, @PathVariable Long messageId) {
        return Mono.empty();
    }

    @DeleteMapping("channels/{channelId}/messages/{messageId}")
    public Mono<MessageDTO> deleteMessage(@PathVariable Long channelId, @PathVariable Long messageId) {
        return Mono.empty();
    }
}
