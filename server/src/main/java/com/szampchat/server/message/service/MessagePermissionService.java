package com.szampchat.server.message.service;

import com.szampchat.server.auth.ResourceTypes;
import com.szampchat.server.auth.preauth.ResourcePermissionEvaluator;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.permission.data.PermissionScope;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service("messagePerm")
public class MessagePermissionService {
    private final MessageService messageService;
    private final ResourcePermissionEvaluator resourcePermission;

    public Mono<Boolean> isOwner(Long channelId, Long messageId, Long userId) {
        return messageService.getMessage(messageId, channelId)
                .map(message -> message.getUser().equals(userId));
    }

    //Have to do it here as spel expression doesn't allow me to combine methods
    public Mono<Boolean> canEdit(Long channelId, Long messageId, Long userId) {
        return resourcePermission.canAccess(channelId, ResourceTypes.CHANNEL)
                .flatMap(canAccess -> {
                    if(canAccess) {
                        return isOwner(channelId, messageId, userId);
                    }
                    return Mono.just(false);
                });
    }

    public Mono<Boolean> canDelete(Long channelId, Long messageId, Long userId) {
        return canEdit(channelId, messageId, userId)
                .flatMap(canEdit -> {
                    if(!canEdit) {
                        return resourcePermission.hasPermission(channelId, PermissionScope.CHANNEL, PermissionFlag.MESSAGE_DELETE);
                    }
                    return Mono.just(true);
                });
    }
}
