package com.szampchat.server.user;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test_save_user() {
        User user1 = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();
        userRepository.save(user1);
    }
}
