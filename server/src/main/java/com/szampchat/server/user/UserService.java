package com.szampchat.server.user;

import com.szampchat.server.snowflake.Snowflake;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.entity.UserSubject;
import com.szampchat.server.user.repository.UserRepository;
import com.szampchat.server.user.repository.UserSubjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserSubjectRepository userSubjectRepository;


    public Mono<User> findUser(Long userId) {
        return userRepository.findById(userId);
    }

    //easy to cache, will practically never change
    public Mono<Long> findUserIdBySub(UUID sub) {
        return userSubjectRepository.findBySub(sub)
                .map(UserSubject::getUserId);
    }

    public Mono<User> findUserBySub(UUID sub) {
        return findUserIdBySub(sub)
                .flatMap(userRepository::findById);
    }

    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }
}
