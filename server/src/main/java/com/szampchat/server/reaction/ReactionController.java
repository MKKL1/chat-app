package com.szampchat.server.reaction;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.reaction.dto.request.ReactionUpdateRequest;
import com.szampchat.server.reaction.service.ReactionService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Validated
@AllArgsConstructor
@RestController
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping("/channels/{channelId}/messages/{messageId}/reactions")
    public Mono<Void> createReaction(@PathVariable Long channelId,
                                     @PathVariable Long messageId,
                                     CurrentUser currentUser,
                                     @RequestBody ReactionUpdateRequest request) {
        return reactionService.createReaction(channelId, messageId, currentUser.getUserId(), request);
    }
//
//    @DeleteMapping("/channels/{channelId}/messages/{messageId}/reactions")
//    public Mono<Void> deleteReaction(@PathVariable Long channelId,
//                                     @PathVariable Long messageId,
//                                     CurrentUser currentUser,
//                                     @RequestBody ReactionUpdateRequest request) {
//        return reactionService.deleteReaction(channelId, messageId, currentUser.getUserId(), request);
//    }
}
