package com.szampchat.server.reaction.service;

import com.szampchat.server.reaction.dto.*;
import com.szampchat.server.reaction.entity.EmojiUserPair;
import com.szampchat.server.reaction.entity.Reaction;
import com.szampchat.server.reaction.entity.ReactionCount;
import com.szampchat.server.reaction.entity.ReactionList;
import com.szampchat.server.reaction.repository.ReactionRepository;
import com.szampchat.server.reaction.repository.redis.ReactionCountRepository;
import com.szampchat.server.reaction.repository.redis.ReactionUsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ReactionCacheService {
    private final ReactionCountRepository reactionCountRepository;
    private final ReactionUsersRepository reactionUsersRepository;

    public Mono<Void> save(Long channelId, Long messageId, Collection<ReactionUsersDTO> reactionUsersDTOS) {
        List<ReactionCount> reactionCounts = reactionUsersDTOS.stream()
                .map(dto -> new ReactionCount(dto.getEmoji(), dto.getUsers().size()))
                .toList();

        List<EmojiUserPair> emojiUserPairs = reactionUsersDTOS.stream()
                .flatMap(dto -> {
                    String emoji = dto.getEmoji();
                    return dto.getUsers().stream()
                            .map(user -> new EmojiUserPair(emoji, user));
                })
                .toList();

        Mono<Boolean> saveCount = reactionCountRepository.save(channelId, messageId, reactionCounts);
        Mono<Boolean> saveUsers = reactionUsersRepository.save(channelId, messageId, emojiUserPairs);

        return Mono.zip(saveCount, saveUsers).then();
    }

    public Mono<Boolean> isCached(Long channelId, Long messageId) {
        //Check both count and users objects, if any of them is invalid return false
        Mono<Boolean> countCached = reactionCountRepository.exists(channelId, messageId);
        Mono<Boolean> usersCached = reactionUsersRepository.exists(channelId, messageId);
        return Mono.zip(countCached, usersCached)
                .map(tuple -> tuple.getT1() && tuple.getT2());
    }

    public Flux<ReactionOverviewDTO> get(Long channelId, Long messageId, Long userId) {
        return reactionCountRepository.get(channelId, messageId)
                .collectList()
                .filter(list -> !list.isEmpty())
                .flatMapMany(reactionCountList -> {
                    List<EmojiUserPair> emojiUserPairs = reactionCountList.stream()
                                .map(reactionCount -> new EmojiUserPair(reactionCount.getEmoji(), userId))
                                .toList();
                    return reactionUsersRepository.contains(channelId, messageId, emojiUserPairs)
                            .flatMapMany(isMemberMap -> Flux.fromIterable(reactionCountList)
                                    .map(reactionCount -> {
                                        String emoji = reactionCount.getEmoji();
                                        return ReactionOverviewDTO.builder()
                                                .emoji(emoji)
                                                .count(reactionCount.getCount())
                                                .me(isMemberMap.get(new EmojiUserPair(emoji, userId)))
                                                .build();
                                    })
                            );
                });
    }
//
//    public Flux<ReactionOverviewBulkDTO> getBulk(Long channelId, Collection<Long> messageIds, Long userId) {
//        return messageIds
//    }

    public Mono<Void> addUserReaction(Reaction reaction) {
        return reactionUsersRepository.add(reaction.getChannel(), reaction.getMessage(), new EmojiUserPair(reaction.getEmoji(), reaction.getUser()))
                .flatMap(_ -> reactionCountRepository.increment(reaction))
                .then();
    }

    public Mono<Void> removeUserReaction(Reaction reaction) {
        return reactionUsersRepository.remove(reaction.getChannel(), reaction.getMessage(), new EmojiUserPair(reaction.getEmoji(), reaction.getUser()))
                .flatMap(_ -> reactionCountRepository.decrement(reaction))
                .then();
    }
}


