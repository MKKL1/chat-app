package com.szampchat.server.voice;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.shared.docs.OperationDocs;
import com.szampchat.server.voice.dto.VoiceTokenResponse;
import io.livekit.server.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import livekit.LivekitModels;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import retrofit2.Call;
import retrofit2.Response;

import static com.szampchat.server.shared.docs.DocsProperties.*;
import static com.szampchat.server.shared.docs.DocsProperties.RESPONSE_401;

@AllArgsConstructor
@RestController
public class VoiceChannelController {
    private final VoiceChannelService voiceChannelService;

    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Join voice channel")

    @GetMapping("/channels/{channelId}/join")
    public Mono<VoiceTokenResponse> joinChannel(@PathVariable("channelId") Long channelId, CurrentUser currentUser) {
        return voiceChannelService.getVoiceTokenResponse(channelId, currentUser.getUserId());
    }
}
