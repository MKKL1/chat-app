package com.szampchat.server.message;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.event.EventSink;
import com.szampchat.server.message.dto.EditMessageDTO;
import com.szampchat.server.message.entity.Message;
import com.szampchat.server.message.event.MessageCreateEvent;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.message.dto.FetchMessagesDTO;
import com.szampchat.server.message.dto.MessageCreateDTO;
import com.szampchat.server.message.dto.MessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "Get messages for given channel")
    @GetMapping("/channels/{channelId}/messages")
    @PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    public Flux<MessageDTO> getMessages(@Parameter(description = "Snowflake ID of text channel", example = "20276884193411072") @PathVariable Long channelId,
                                        @ParameterObject FetchMessagesDTO fetchMessagesDTO,
                                        CurrentUser currentUser) {
        return messageService.getMessages(channelId, fetchMessagesDTO, currentUser.getUserId());
    }

    @PostMapping("/channels/{channelId}/messages")
    @PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    public Mono<Message> createMessage(@PathVariable Long channelId, @RequestBody MessageCreateDTO messageCreateDTO, CurrentUser currentUser) {
        return messageService.createMessage(messageCreateDTO, currentUser.getUserId(), channelId);
    }

    // maybe just check if user created this message
    // Only think which makes sense to change is text
    // We don't want to do changing images, responds etc.
    // TODO message is inserted instead of update
    @PatchMapping("/channels/{channelId}/messages/{messageId}")
    @PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    public Mono<Message> editMessage(@PathVariable Long channelId, @RequestBody EditMessageDTO editMessage, @PathVariable Long messageId, CurrentUser currentUser) {
        return messageService.editMessage(editMessage.text(), messageId, currentUser.getUserId());
    }

    // maybe just check if user created this message
    // TODO Delete file if attached
    @DeleteMapping("channels/{channelId}/messages/{messageId}")
    @PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    public Mono<Void> deleteMessage(@PathVariable Long channelId, @PathVariable Long messageId, CurrentUser currentUser) {
        return messageService.deleteMessage(messageId, currentUser.getUserId());
    }
}
