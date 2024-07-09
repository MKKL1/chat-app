package com.szampchat.server.user;

import com.szampchat.server.community.CommunityMember;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Mono<User> findUser(Long userId) {
        return userRepository.findById(userId);
    }
}
