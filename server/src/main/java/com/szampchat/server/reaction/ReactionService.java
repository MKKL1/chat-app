package com.szampchat.server.reaction;

import com.szampchat.server.reaction.dto.ReactionOverviewBulkDTO;
import com.szampchat.server.reaction.dto.ReactionUsersBulkDTO;
import com.szampchat.server.reaction.dto.ReactionUsersDTO;
import com.szampchat.server.reaction.dto.ReactionOverviewDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ReactionService {
    private final ReactionCacheService reactionCacheService;

    public Flux<ReactionOverviewDTO> getReactionsForUser(Long channelId, Long messageId, Long userId) {
        return reactionCacheService.fetchAndCacheReactions(channelId, messageId, userId);
    }

    public Flux<ReactionOverviewBulkDTO> getReactionsForUserBulk(Long channelId, Collection<Long> messageIds, Long userId) {
        return reactionCacheService.fetchAndCacheReactionsBulk(channelId, messageIds, userId);
    }
}

