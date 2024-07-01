package com.szampchat.server.message.repository;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.message.base.entity.Message;
import com.szampchat.server.message.base.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.awt.print.Pageable;
import java.util.List;

@SpringBootTest
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void find_messages_by_channel_id() {
        List<Message> latestMessagesForChannel = messageRepository.findLatestMessagesForChannel(Channel.builder()
                .id(18815672880463872L)
                .build().getId(), 5);
        System.out.println("siema");
    }
}
