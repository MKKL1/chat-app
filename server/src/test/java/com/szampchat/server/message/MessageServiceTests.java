package com.szampchat.server.message;

import com.szampchat.server.message.base.GetMessagesRequest;
import com.szampchat.server.message.base.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MessageServiceTests {
    @Autowired
    private MessageService messageService;

    @Test
    void fetchMessagesTest() {
        GetMessagesRequest getMessagesRequest = new GetMessagesRequest();
        messageService.getMessages(8507337136406528L, getMessagesRequest);
    }
}
