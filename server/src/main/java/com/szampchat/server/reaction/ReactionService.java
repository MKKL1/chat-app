package com.szampchat.server.reaction;

import com.szampchat.server.reaction.dto.ReactionDTO;
import com.szampchat.server.reaction.dto.ReactionOverviewDTO;
import com.szampchat.server.reaction.repository.ReactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ReactionService {
    private final ReactionCacheService reactionCacheService;

    public Flux<ReactionOverviewDTO> getReactionsForUser(Long channelId, Long messageId, Long userId) {
        return reactionCacheService.fetchAndCacheReactions(channelId, messageId)
                .flatMapMany(reactions -> {
                    // Transform ReactionDTO list into ReactionOverviewDTO list
                    Map<String, Set<Long>> emojiUserMap = reactions.stream()
                            .collect(Collectors.toMap(
                                    ReactionDTO::getEmoji,
                                    ReactionDTO::getUsers
                            ));

                    // Build the ReactionOverviewDTO list
                    return Flux.fromIterable(emojiUserMap.entrySet())
                            .map(entry -> {
                                String emoji = entry.getKey();
                                Set<Long> users = entry.getValue();
                                int count = users.size();
                                boolean me = users.contains(userId);

                                ReactionOverviewDTO overview = new ReactionOverviewDTO();
                                overview.setEmoji(emoji);
                                overview.setCount(count);
                                overview.setMe(me);
                                return overview;
                            });
                });
    }
}

