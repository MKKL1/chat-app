package com.szampchat.server.reaction;

import com.szampchat.server.reaction.dto.ReactionOverviewBulkDTO;
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
        return reactionCacheService.fetchAndCacheReactions(channelId, messageId)
                .flatMapMany(reactions -> {
                    // Transform ReactionDTO list into ReactionOverviewDTO list
                    return addUserInfo(reactions, userId);
                });
    }

    public Flux<ReactionOverviewBulkDTO> getReactionsForUserBulk(Long channelId, Collection<Long> messageIds, Long userId) {
        return reactionCacheService.fetchAndCacheReactionsBulk(channelId, messageIds)
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .flatMap(entry -> addUserInfo(entry.getValue(), userId).collectList()
                        .map(list -> new ReactionOverviewBulkDTO(entry.getKey(), list))
                );
    }

    private Flux<ReactionOverviewDTO> addUserInfo(Collection<ReactionUsersDTO> reactionUsersDTOS, Long userId) {
        Map<String, Set<Long>> emojiUserMap = reactionUsersDTOS.stream()
                .collect(Collectors.toMap(
                        ReactionUsersDTO::getEmoji,
                        ReactionUsersDTO::getUsers
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
    }
}

