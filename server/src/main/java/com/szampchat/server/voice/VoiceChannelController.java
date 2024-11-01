package com.szampchat.server.voice;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.shared.docs.OperationDocs;
import com.szampchat.server.voice.dto.VoiceTokenResponse;
import com.szampchat.server.voice.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.szampchat.server.shared.docs.DocsProperties.*;
import static com.szampchat.server.shared.docs.DocsProperties.RESPONSE_401;

@AllArgsConstructor
@RestController
public class VoiceChannelController {
    private final RoomService roomService;

    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Join voice channel")

    @GetMapping("/channels/{channelId}/voice/join")
    public Mono<VoiceTokenResponse> joinChannel(@PathVariable("channelId") Long channelId, CurrentUser currentUser) {
        return roomService.getVoiceTokenResponse(channelId, currentUser.getUserId());
    }
}
