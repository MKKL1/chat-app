package com.szampchat.server.message;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.auth.annotation.HasPermission;
import com.szampchat.server.auth.annotation.ResourceId;
import com.szampchat.server.message.dto.*;
import com.szampchat.server.message.dto.request.MessageEditRequest;
import com.szampchat.server.message.dto.request.FetchMessagesRequest;
import com.szampchat.server.message.dto.request.MessageCreateRequest;
import com.szampchat.server.message.entity.Message;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.permission.data.PermissionScope;
import com.szampchat.server.shared.docs.OperationDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.szampchat.server.shared.docs.DocsProperties.*;

@Tag(name = "Message")
@SecurityRequirement(name = "OAuthSecurity")

@Validated
@AllArgsConstructor
@RestController
public class MessageController {
    private final MessageService messageService;


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get channel's messages")

    @PreAuthorize("@auth.canAccess(#channelId, 'CHANNEL')")
    @GetMapping("/channels/{channelId}/messages")
    public Flux<MessageDTO> getMessages(@PathVariable Long channelId,
                                        @ParameterObject FetchMessagesRequest fetchMessagesRequest,
                                        CurrentUser currentUser) {
        return messageService.getMessages(channelId, fetchMessagesRequest, currentUser.getUserId());
    }


    @ApiResponse(responseCode = "201")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Create message")

    @HasPermission(scope = PermissionScope.CHANNEL, value = PermissionFlag.MESSAGE_CREATE)
    @PreAuthorize("@auth.canAccess(#channelId, 'CHANNEL')")
    @PostMapping("/channels/{channelId}/messages")
    public Mono<MessageDTO> createMessage(@ResourceId @PathVariable Long channelId,
                                       @RequestPart("message") MessageCreateRequest messageCreateRequest,
                                       @RequestPart(value = "file", required = false) FilePart file,
                                       CurrentUser currentUser) {
        return messageService.createMessage(messageCreateRequest, currentUser.getUserId(), channelId, file);
    }


    // maybe just check if user created this message
    // Only think which makes sense to change is text
    // We don't want to do changing images, responds etc.
    // TODO message is inserted instead of update
    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Edit message")

    //TODO add is owner of message
    @PreAuthorize("@auth.canAccess(#channelId, 'CHANNEL')")
    @PatchMapping("/channels/{channelId}/messages/{messageId}")
    public Mono<Message> editMessage(@PathVariable Long channelId, @RequestBody MessageEditRequest editMessage, @PathVariable Long messageId, CurrentUser currentUser) {
        return messageService.editMessage(messageId, channelId, editMessage.text(), currentUser.getUserId());
    }


    // TODO Delete file if attached
    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Delete message")

    //TODO add is owner of message or has message delete permission
    @DeleteMapping("channels/{channelId}/messages/{messageId}")
    public Mono<Void> deleteMessage(@PathVariable Long channelId, @PathVariable Long messageId, CurrentUser currentUser) {
        return messageService.deleteMessage(messageId, channelId, currentUser.getUserId());
    }
}
