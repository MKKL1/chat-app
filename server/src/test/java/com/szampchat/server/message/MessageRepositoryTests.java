package com.szampchat.server.message;

import com.szampchat.server.message.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MessageRepositoryTests {
    @Autowired
    private MessageRepository messageRepository;

    
}
