package com.szampchat.server.message.base;

import com.szampchat.server.message.base.entity.Message;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController("/api")
@AllArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/channel/{channelId}/messages")
    public Flux<Message> getMessages(@PathVariable Long channelId, GetMessagesRequest getMessagesRequest) {
        return messageService.getMessages(channelId, getMessagesRequest);
    }
}
