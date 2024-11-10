package com.szampchat.server.reaction;

import com.szampchat.server.reaction.dto.*;
import com.szampchat.server.reaction.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class ReactionCacheService {
    private final ReactionRepository reactionRepository;
    private final ReactiveRedisTemplate<String, String> redisStringTemplate;
    private final ReactiveRedisTemplate<String, ReactionListDTO> redisReactionTemplate;

    public ReactionCacheService(ReactionRepository reactionRepository,
                                @Qualifier("stringReactiveRedisTemplate") ReactiveRedisTemplate<String, String> redisStringTemplate,
                                @Qualifier("reactionListDTOReactiveRedisTemplate") ReactiveRedisTemplate<String, ReactionListDTO> redisReactionTemplate) {
        this.reactionRepository = reactionRepository;
        this.redisStringTemplate = redisStringTemplate;
        this.redisReactionTemplate = redisReactionTemplate;
    }

    private static final String REACTIONS_CACHE_NAME = "emojis:";
    private static final String REACTIONS_USERS_CACHE_NAME = "reactions:";

    private Mono<Void> cacheReactions(Long channelId, Long messageId, List<ReactionUsersDTO> reactionUsersDTOS) {
        final String cacheKeyReaction = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        final String cacheKeyUserSet = REACTIONS_USERS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;

        ReactiveSetOperations<String, String> setOpsString = redisStringTemplate.opsForSet();
        ReactiveValueOperations<String, ReactionListDTO> valueOpsReaction = redisReactionTemplate.opsForValue();

        Mono<Boolean> saveReactionDto = Flux.fromIterable(reactionUsersDTOS)
                .map(dto -> new ReactionCountDTO(dto.getEmoji(), dto.getUsers().size()))
                .collectList()
                .flatMap(list -> valueOpsReaction.set(cacheKeyReaction, new ReactionListDTO(list)));

        Mono<Long> saveUsers = Flux.fromIterable(reactionUsersDTOS)
                .flatMap(dto -> {
                    String emoji = dto.getEmoji();
                    return Flux.fromStream(dto.getUsers().stream().map(user -> combineUserAndEmoji(emoji, user)));
                })
                .collectList()
                .flatMap(reactionValueList -> setOpsString.add(cacheKeyUserSet, reactionValueList.toArray(new String[0])));

        return Mono.zip(saveReactionDto, saveUsers).then();
    }

    public Flux<ReactionOverviewDTO> fetchAndCacheReactions(Long channelId, Long messageId, Long userId) {
        final String cacheKeyEmojiSet = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        return redisStringTemplate.hasKey(cacheKeyEmojiSet)
                .flatMapMany(hasKey -> {
                    if(hasKey) {
                        return getFromCache(channelId, messageId, userId);
                    } else {
                        return getFromDB(channelId, messageId, userId);
                    }
                });
    }

    private Flux<ReactionOverviewDTO> getFromCache(Long channelId, Long messageId, Long userId) {
        final String cacheKeyReaction = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        final String cacheKeyUserSet = REACTIONS_USERS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;

        ReactiveSetOperations<String, String> setOpsString = redisStringTemplate.opsForSet();
        ReactiveValueOperations<String, ReactionListDTO> valueOpsReaction = redisReactionTemplate.opsForValue();

        return valueOpsReaction.get(cacheKeyReaction)
                .map(ReactionListDTO::getReactions)
                .flatMapMany(reactionCounts -> {
                    Map<String, String> userEmojis = new HashMap<>(); //Map of emoji-> "emoji:user"
                    for (ReactionCountDTO reactionCountDTO : reactionCounts) {
                        userEmojis.put(reactionCountDTO.getEmoji(), combineUserAndEmoji(reactionCountDTO.getEmoji(), userId));
                    }

                    return setOpsString.isMember(cacheKeyUserSet, userEmojis.values().toArray()) //Returns Map of "emoji:user"->me
                            .flatMapMany(isMeMap -> Flux.fromIterable(reactionCounts)
                                    .map(reactionCountDTO -> {
                                        String emoji = reactionCountDTO.getEmoji();
                                        String emojiUserKey = userEmojis.get(emoji);
                                        boolean isMe = isMeMap.get(emojiUserKey);
                                        return new ReactionOverviewDTO(emoji, reactionCountDTO.getCount(), isMe);
                                    })
                            );
                });
    }

    private String combineUserAndEmoji(String emoji, Long userId) {
        return emoji + ":" + userId;
    }

    private Flux<ReactionOverviewDTO> getFromDB(Long channelId, Long messageId, Long userId) {
        return reactionRepository.fetchGroupedReactions(channelId, messageId)
                .collectList()
                .flatMap(reactionUsersDTOS -> cacheReactions(channelId, messageId, reactionUsersDTOS)
                        .then(Mono.just(reactionUsersDTOS))
                )
                .flatMapMany(Flux::fromIterable)
                .map(dto -> {
                    int count = dto.getUsers().size();
                    boolean me = dto.getUsers().contains(userId);
                    return new ReactionOverviewDTO(dto.getEmoji(), count, me);
                });
    }

//    public Mono<Map<Long, Collection<ReactionUsersDTO>>> fetchAndCacheReactionsBulk(Long channelId, Collection<Long> messageIds) {
//        List<String> cacheKeys = messageIds.stream()
//                .map(messageId -> REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId)
//                .toList();
//
//        //TODO add comments
//        return redisTemplate.opsForValue().multiGet(cacheKeys)
//                .flatMap(cachedEntries -> {
//                    Map<Long, Collection<ReactionUsersDTO>> cachedResults = new HashMap<>();
//                    Iterator<Long> messageIdIterator = messageIds.iterator();
//
//                    List<Long> missingIds = new ArrayList<>();
//                    for (ReactionListDTO dto : cachedEntries) {
//                        Long messageId = messageIdIterator.next();
//
//                        if (dto != null && dto.getReactions() != null)
//                            cachedResults.put(messageId, dto.getReactions());
//
//                        else missingIds.add(messageId);
//
//                    }
//
//                    if (missingIds.isEmpty()) return Mono.just(cachedResults);
//
//                    return reactionRepository.fetchGroupedReactionsBulk(channelId, missingIds)
//                            .collectMultimap(ReactionUsersBulkDTO::getMessageId,
//                                    reactionUsersBulkDTO -> new ReactionUsersDTO(
//                                            reactionUsersBulkDTO.getEmoji(),
//                                            reactionUsersBulkDTO.getUsers()
//                                    )
//                            )
//                            .flatMap(map -> Flux.fromIterable(map.entrySet())
//                                            .map(entry ->
//                                                    Map.entry(
//                                                            REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + entry.getKey(),
//                                                            new ReactionListDTO(entry.getValue().stream().toList())
//                                                    )
//                                            )
//                                            .collectMap(Map.Entry::getKey, Map.Entry::getValue)
//                                            .flatMap(missingReactions -> redisTemplate.opsForValue().multiSet(missingReactions))
//                                            .thenReturn(map)
//                            ).map(map -> Stream.concat(
//                                            cachedResults.entrySet().stream(),
//                                            map.entrySet().stream()
//                                    ).collect(Collectors.<Map.Entry<Long, Collection<ReactionUsersDTO>>, Long, Collection<ReactionUsersDTO>>toMap(
//                                            Map.Entry::getKey,
//                                            Map.Entry::getValue,
//                                            (existing, replacement) -> replacement
//                                            ))
//                            );
//                });
//    }
}


