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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private Mono<Void> cacheReactions(Long channelId, Long messageId, Collection<ReactionUsersDTO> reactionUsersDTOS) {
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
        ReactiveValueOperations<String, ReactionListDTO> valueOpsReaction = redisReactionTemplate.opsForValue();

        return valueOpsReaction.get(cacheKeyReaction)
                .flatMapMany(list -> processReactionList(channelId, messageId, userId, list));
    }

    private Flux<ReactionOverviewDTO> processReactionList(Long channelId, Long messageId, Long userId, ReactionListDTO reactionListDTO) {
        final String cacheKeyUserSet = REACTIONS_USERS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        ReactiveSetOperations<String, String> setOpsString = redisStringTemplate.opsForSet();

        return Mono.just(reactionListDTO)
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
                //TODO Could be zipped
                .flatMap(reactionUsersDTOS -> cacheReactions(channelId, messageId, reactionUsersDTOS)
                        .then(Mono.just(reactionUsersDTOS))
                )
                .flatMapMany(list -> reactionUsersToOverview(list, userId));
    }

    private Flux<ReactionOverviewDTO> reactionUsersToOverview(Collection<ReactionUsersDTO> reactionUsersDTOS, Long userId) {
        return Flux.fromIterable(reactionUsersDTOS)
                .map(dto -> {
                    int count = dto.getUsers().size();
                    boolean me = dto.getUsers().contains(userId);
                    return new ReactionOverviewDTO(dto.getEmoji(), count, me);
                });
    }

    public Flux<ReactionOverviewBulkDTO> fetchAndCacheReactionsBulk(Long channelId, Collection<Long> messageIds, Long userId) {
        List<String> cacheKeys = messageIds.stream()
                .map(messageId -> REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId)
                .toList();

        ReactiveValueOperations<String, ReactionListDTO> valueOpsReaction = redisReactionTemplate.opsForValue();

        return valueOpsReaction.multiGet(cacheKeys)
                .flatMapMany(cachedResults -> {
                    // Map cache results to messageIds
                    Map<Long, ReactionListDTO> cachedMap = new HashMap<>();
                    Iterator<Long> messageIdIterator = messageIds.iterator();

                    for (ReactionListDTO cachedResult : cachedResults) {
                        Long messageId = messageIdIterator.next();
                        if (cachedResult != null) {
                            cachedMap.put(messageId, cachedResult);
                        }
                    }

                    // Filter out messageIds that need fetching from DB
                    List<Long> missingMessageIds = messageIds.stream()
                            .filter(messageId -> !cachedMap.containsKey(messageId))
                            .toList();

                    // Fetch missing data from DB, cache it, and process it
                    Flux<ReactionOverviewBulkDTO> dbResults = reactionRepository.fetchGroupedReactionsBulk(channelId, missingMessageIds)
                            .collectList()
                            .flatMapMany(bulkReactions -> {
                                Map<Long, List<ReactionUsersDTO>> reactionsByMessage = bulkReactions.stream()
                                        .collect(Collectors.groupingBy(
                                                ReactionUsersBulkDTO::getMessageId,
                                                Collectors.mapping(
                                                        dto -> new ReactionUsersDTO(dto.getEmoji(), dto.getUsers()),
                                                        Collectors.toList()
                                                )
                                        ));

                                return Flux.fromIterable(reactionsByMessage.entrySet())
                                        .flatMap(entry -> {
                                            Long messageId = entry.getKey();
                                            List<ReactionUsersDTO> reactionUsersDTOS = entry.getValue();

                                            return cacheReactions(channelId, messageId, reactionUsersDTOS)
                                                    .then(reactionUsersToOverview(reactionUsersDTOS, userId)
                                                            .collectList()
                                                            .map(list -> new ReactionOverviewBulkDTO(messageId, list))
                                                    );
                                        });
                            });

                    Flux<ReactionOverviewBulkDTO> cachedResultsFlux = Flux.fromIterable(cachedMap.entrySet())
                            .flatMap(entry -> processReactionList(channelId, entry.getKey(), userId, entry.getValue())
                                    .collectList()
                                    .map(list -> new ReactionOverviewBulkDTO(entry.getKey(), list)));

                    // Combine cached and fetched data
                    return Flux.concat(cachedResultsFlux, dbResults);
                });
    }
}


