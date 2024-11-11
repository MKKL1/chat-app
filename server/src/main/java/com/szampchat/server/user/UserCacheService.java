package com.szampchat.server.user;

import com.szampchat.server.user.entity.UserSubject;
import com.szampchat.server.user.repository.UserSubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@Service
public class UserCacheService {
    private final UserSubjectRepository userSubjectRepository;

    @Cacheable(value = "usersub", key = "#sub", cacheManager = "caffeineCacheManager")
    //easy to cache, will practically never change
    public Mono<Long> findUserIdBySub(UUID sub) {
        return userSubjectRepository.findBySub(sub)
                .map(UserSubject::getUserId)
                .cache();
    }
}
