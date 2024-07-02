package com.szampchat.server.message.base;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.channel.repository.ChannelRepository;
import com.szampchat.server.message.base.entity.Message;
import com.szampchat.server.message.base.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;

    public Flux<Message> findLatestMessagesForChannel(Long channelId, int limit) {
        return channelRepository.findById(channelId)
                .flatMapMany(messageRepository::findAllByChannel);
//        return Flux.empty();
    }
}
