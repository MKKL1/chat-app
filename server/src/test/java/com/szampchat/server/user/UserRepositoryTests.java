package com.szampchat.server.user;
import com.szampchat.server.community.Community;
import com.szampchat.server.community.CommunityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Test
    public void test_save_user() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user1 = User.builder()
                    .name(STR."John Doe\{i}")
                    .email(STR."john.doe\{i}@example.com")
                    .password("password123")
                    .build();
            userRepository.save(user1);
            users.add(user1);
        }

        for (int i = 0; i < 2; i++) {
            Collections.shuffle(users);
            Community community = Community.builder()
                    .name("community")
                    .members(users.stream().limit(5).collect(Collectors.toSet()))
                    .build();
            communityRepository.save(community);
        }

    }
}
