package com.szampchat.server.message.base;

import com.szampchat.server.message.base.entity.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController("/api")
@AllArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/channel/{channelId}/messages")
    public Flux<Message> getMessages(@PathVariable Long channelId, @RequestParam(value = "limit", required = false) Integer limit) {
        return Mono.justOrEmpty(limit)
                .switchIfEmpty(Mono.just(10))
                .flatMapMany(l -> messageService.findLatestMessagesForChannel(channelId, l));
    }
}
