package com.szampchat.server.community;

import com.szampchat.server.PostgresTestContainer;
import com.szampchat.server.RabbitMQTestContainer;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.tools.WithMockCustomUser;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CommunityControllerIT implements PostgresTestContainer, RabbitMQTestContainer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void beforeAll() {
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        postgres.followOutput(logConsumer);
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

    @Test
    void givenAnonymousUser_whenGetCommunity_thenUnauthorized() {
        webTestClient.get().uri("/api/communities/1").exchange()
                .expectStatus().isUnauthorized();
    }

    @WithMockCustomUser
    @Test
    void givenAuthenticatedUserAndNotDefinedCommunity_whenGetCommunity_thenNotFound() {
        webTestClient.get().uri("/api/communities/1").exchange()
                .expectStatus().isNotFound();
    }

    @WithMockCustomUser
    @Test
    void givenAuthenticatedUserAndNotDefinedCommunity_whenGetCommunity_thenReturnCommunityDTO() {

        //Relation doesn't exist //TODO fix
        User owner = userRepository.save(User.builder()
                        .description("")
                        .username("da")
                .build()).block();

        Community community = communityRepository.save(Community.builder()
                        .name("adad")
                        .ownerId(owner.getId())
                        .imageUrl("")
                .build()).block();


        webTestClient.get().uri("/api/communities/" + community.getId()).exchange()
                .expectStatus().isOk()
                .expectBody(Community.class) //TODO It really should be CommunityDTO
                .value((res) -> assertThat(res.getId()).isEqualTo(1L));
    }
}
