package com.szampchat.server.reaction;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.auth.annotation.HasPermission;
import com.szampchat.server.auth.annotation.ResourceId;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.permission.data.PermissionScope;
import com.szampchat.server.reaction.dto.request.ReactionUpdateRequest;
import com.szampchat.server.reaction.service.ReactionService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Validated
@AllArgsConstructor
@RestController
public class ReactionController {
    private final ReactionService reactionService;

    @HasPermission(scope = PermissionScope.CHANNEL, value = PermissionFlag.REACTION_CREATE)
    @PreAuthorize("@auth.canAccess(#channelId, 'CHANNEL')")
    @PostMapping("/channels/{channelId}/messages/{messageId}/reactions")
    public Mono<Void> createReaction(@PathVariable Long channelId,
                                     @ResourceId @PathVariable Long messageId,
                                     CurrentUser currentUser,
                                     @RequestBody ReactionUpdateRequest request) {
        return reactionService.createReaction(channelId, messageId, currentUser.getUserId(), request);
    }

    @PreAuthorize("@auth.canAccess(#channelId, 'CHANNEL')")
    @DeleteMapping("/channels/{channelId}/messages/{messageId}/reactions")
    public Mono<Void> deleteReaction(@PathVariable Long channelId,
                                     @PathVariable Long messageId,
                                     CurrentUser currentUser,
                                     @RequestBody ReactionUpdateRequest request) {
        return reactionService.deleteReaction(channelId, messageId, currentUser.getUserId(), request);
    }
}
