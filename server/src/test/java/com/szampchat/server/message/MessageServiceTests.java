package com.szampchat.server.message;

import com.szampchat.server.message.dto.FetchMessagesDTO;
import com.szampchat.server.message.dto.MessageDTO;
import com.szampchat.server.message.entity.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MessageServiceTests {

    @InjectMocks
    @Spy
    private MessageService messageService;

    @Test
    void getMessages_LatestMessages() {
        Long channelId = 1L;
        Long currentUserId = 123L;
        Integer limit = 5;
        FetchMessagesDTO fetchMessagesDTO = new FetchMessagesDTO();
        fetchMessagesDTO.setLimit(limit);
        fetchMessagesDTO.setBefore(null);

        Message message1 = new Message();
        Message message2 = new Message();
        MessageDTO messageDTO1 = new MessageDTO();
        MessageDTO messageDTO2 = new MessageDTO();

        doReturn(Flux.just(message1, message2))
                .when(messageService).findLatestMessages(eq(channelId), eq(limit));

        doReturn(Mono.just(messageDTO1)).when(messageService).attachAdditionalDataToMessage(eq(message1), eq(currentUserId));
        doReturn(Mono.just(messageDTO2)).when(messageService).attachAdditionalDataToMessage(eq(message2), eq(currentUserId));


        Flux<MessageDTO> result = messageService.getMessages(channelId, fetchMessagesDTO, currentUserId);


        StepVerifier.create(result)
                .expectNext(messageDTO1)
                .expectNext(messageDTO2)
                .verifyComplete();
    }

    @Test
    void getMessages_BeforeSpecificMessage() {
        Long channelId = 1L;
        Long currentUserId = 123L;
        Integer limit = 5;
        Long messageId = 100L;
        FetchMessagesDTO fetchMessagesDTO = new FetchMessagesDTO();
        fetchMessagesDTO.setLimit(limit);
        fetchMessagesDTO.setBefore(messageId); //not actual snowflake ID

        Message message1 = new Message();
        Message message2 = new Message();
        MessageDTO messageDTO1 = new MessageDTO();
        MessageDTO messageDTO2 = new MessageDTO();

        doReturn(Flux.just(message1, message2))
                .when(messageService).findMessagesBefore(eq(channelId), eq(messageId), eq(limit));

        doReturn(Mono.just(messageDTO1)).when(messageService).attachAdditionalDataToMessage(eq(message1), eq(currentUserId));
        doReturn(Mono.just(messageDTO2)).when(messageService).attachAdditionalDataToMessage(eq(message2), eq(currentUserId));

        Flux<MessageDTO> result = messageService.getMessages(channelId, fetchMessagesDTO, currentUserId);

        StepVerifier.create(result)
                .expectNext(messageDTO1)
                .expectNext(messageDTO2)
                .verifyComplete();
    }

    @Test
    void getMessages_DefaultLimit() {
        Long channelId = 1L;
        Long currentUserId = 123L;
        FetchMessagesDTO fetchMessagesDTO = new FetchMessagesDTO();
        fetchMessagesDTO.setLimit(null);
        fetchMessagesDTO.setBefore(null);

        Message message1 = new Message();
        Message message2 = new Message();
        MessageDTO messageDTO1 = new MessageDTO();
        MessageDTO messageDTO2 = new MessageDTO();

        doReturn(Flux.just(message1, message2))
                .when(messageService).findLatestMessages(eq(channelId), eq(10)); // Default limit is 10

        doReturn(Mono.just(messageDTO1)).when(messageService).attachAdditionalDataToMessage(eq(message1), eq(currentUserId));
        doReturn(Mono.just(messageDTO2)).when(messageService).attachAdditionalDataToMessage(eq(message2), eq(currentUserId));

        Flux<MessageDTO> result = messageService.getMessages(channelId, fetchMessagesDTO, currentUserId);

        StepVerifier.create(result)
                .expectNext(messageDTO1)
                .expectNext(messageDTO2)
                .verifyComplete();
    }
}
