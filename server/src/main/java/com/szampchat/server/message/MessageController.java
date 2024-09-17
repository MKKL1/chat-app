package com.szampchat.server.message;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.event.EventSinkService;
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
    private final ChannelService channelService; //For test
    private final EventSinkService eventSender;

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
    public Mono<MessageDTO> createMessage(@PathVariable Long channelId, MessageCreateDTO messageCreateDTO, CurrentUser currentUser) {
        //TODO replace with actual message creation
        return channelService.getChannel(channelId)
                .map(channel -> {
                    MessageDTO message = MessageDTO.builder() //TODO Use data from messageCreateDTO...
                            .id(123L)
                            .text("TEST TEXT")
                            .user(currentUser.getUserId())
                            .channel(channelId)
                            .build();
                    eventSender.publish(MessageCreateEvent.builder()
                            .data(message)
                            .recipient(Recipient.builder()
                                    .context(Recipient.Context.COMMUNITY)
                                    .id(channel.getCommunityId())
                                    .build())
                            .build());

                    return message;
                });
    }

    @PatchMapping("/channels/{channelId}/messages/{messageId}")
    @PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    public Mono<MessageDTO> editMessage(@PathVariable Long channelId, @PathVariable Long messageId, CurrentUser currentUser) {
        return Mono.empty();
    }

    @DeleteMapping("channels/{channelId}/messages/{messageId}")
    @PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    public Mono<MessageDTO> deleteMessage(@PathVariable Long channelId, @PathVariable Long messageId, CurrentUser currentUser) {
        return Mono.empty();
    }
}
