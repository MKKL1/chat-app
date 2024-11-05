package com.szampchat.server.message;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.event.EventSink;
import com.szampchat.server.message.dto.*;
import com.szampchat.server.message.entity.Message;
import com.szampchat.server.message.event.MessageCreateEvent;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.shared.docs.OperationDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.szampchat.server.shared.docs.DocsProperties.*;

@Tag(name = "Message")
@SecurityRequirement(name = "OAuthSecurity")

@AllArgsConstructor
@RestController
public class MessageController {
    private final MessageService messageService;


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get channel's messages")

    @GetMapping("/channels/{channelId}/messages")
//    @PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    public Flux<MessageDTO> getMessages(@PathVariable Long channelId,
                                        @ParameterObject FetchMessagesDTO fetchMessagesDTO,
                                        CurrentUser currentUser) {
        return messageService.getMessages(channelId, fetchMessagesDTO, currentUser.getUserId());
    }


//    @ApiResponse(responseCode = "200")
//    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
//    @Operation(summary = "Get messages by id", description = """
//            Endpoint that allows for retrieving multiple messages, by list of ids provided in request body.
//            Could be used for attaching initial message body, that some message is responding to.
//
//            It uses POST, but it could be changed to GET if needed
//            """)
//
//    @PostMapping("/channels/{channelId}/messages/")
//    public Flux<MessageDTO> getMessagesById(@PathVariable Long channelId,
//                                            @RequestBody MessageBulkRequest  messageBulkRequest,
//                                            CurrentUser currentUser) {
//        return messageService.getMessagesBulk(channelId, messageBulkRequest, currentUser.getUserId());
//    }


    @ApiResponse(responseCode = "201")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Create message")

    @PostMapping("/channels/{channelId}/messages")
    //@PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    public Mono<MessageDTO> createMessage(@PathVariable Long channelId,
                                       @RequestPart("message") MessageCreateDTO messageCreateDTO,
                                       @RequestPart(value = "file", required = false) FilePart file,
                                       CurrentUser currentUser) {
        return messageService.createMessage(messageCreateDTO, currentUser.getUserId(), channelId, file);
    }


    // maybe just check if user created this message
    // Only think which makes sense to change is text
    // We don't want to do changing images, responds etc.
    // TODO message is inserted instead of update
    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Edit message")

    @PatchMapping("/channels/{channelId}/messages/{messageId}")
//    @PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    public Mono<Message> editMessage(@PathVariable Long channelId, @RequestBody EditMessageDTO editMessage, @PathVariable Long messageId, CurrentUser currentUser) {
        return messageService.editMessage(messageId, channelId, editMessage.text(), currentUser.getUserId());
    }


    // maybe just check if user created this message
    // TODO Delete file if attached
    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Delete message")

    @DeleteMapping("channels/{channelId}/messages/{messageId}")
    //@PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    public Mono<Void> deleteMessage(@PathVariable Long channelId, @PathVariable Long messageId, CurrentUser currentUser) {
        return messageService.deleteMessage(messageId, channelId, currentUser.getUserId());
    }
}
