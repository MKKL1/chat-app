package com.szampchat.server.community;

import com.szampchat.server.PostgresTestContainer;
import com.szampchat.server.RabbitMQTestContainer;
import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.tools.TestSecurityConfig;
import com.szampchat.server.tools.WithMockCustomUser;
import com.szampchat.server.user.UserController;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Testcontainers
@AutoConfigureWebTestClient(timeout = "3600000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestSecurityConfig.class)
public class CommunityControllerIT implements PostgresTestContainer, RabbitMQTestContainer {

    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected WebTestClient client;


    @BeforeAll
    static void beforeAll() {
        postgres.start();
        rabbitmq.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
        rabbitmq.stop();
    }

    @Test
    @Override
    public void connectionEstablished() {
        PostgresTestContainer.super.connectionEstablished();
        RabbitMQTestContainer.super.connectionEstablished();
    }

    @WithMockCustomUser
    @Test
    void givenAuthenticatedUserAndNotDefinedCommunity_whenGetCommunity_thenNotFound() {
        client.get().uri("/communities/1").exchange()
                .expectStatus().isNotFound();
    }

    @WithMockCustomUser
    @Test
    void givenAuthenticatedUserAndDefinedCommunity_whenGetCommunity_thenReturnCommunityDTO() {

        User owner = userRepository.save(User.builder()
                        .description("")
                        .username("da")
                .build()).block();


        Community community = communityRepository.save(Community.builder()
                        .name("adad")
                        .ownerId(owner.getId())
                        .imageUrl("")
                .build()).block();

        client.get().uri("/communities/" + community.getId()).exchange()
                .expectStatus().isOk()
                .expectBody(Community.class) //TODO It really should be CommunityDTO
                .value((res) -> assertThat(res.getId()).isEqualTo(1L));
    }
}
