package com.szampchat.server.user;

import com.szampchat.server.snowflake.Snowflake;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final Snowflake snowflake;

    public Mono<User> findUser(Long userId) {
        return userRepository.findById(userId);
    }

    public Mono<User> createUser(User user) {
        return Mono.just(user)
                .map(u -> { //note to self: check if this operation will be performed asynchronously
                    if (u.getId() == null) {
                        u.setId(snowflake.nextId());
                    }
                    return u;
                })
                .flatMap(userRepository::save);
    }
}
