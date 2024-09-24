package com.szampchat.server.community;

import com.szampchat.server.PostgresTestContainer;
import com.szampchat.server.RabbitMQTestContainer;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.tools.WithMockCustomUser;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Slf4j
@Testcontainers
@AutoConfigureWebTestClient(timeout = "3600000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class CommunityControllerIT implements PostgresTestContainer, RabbitMQTestContainer {

    @MockBean
    private CommunityMemberService communityMemberService;

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
        when(communityMemberService.isMember(anyLong(), anyLong())).thenReturn(Mono.just(true));

        client.get().uri("/communities/1").exchange()
                .expectStatus().isNotFound();
    }

    @WithMockCustomUser
    @Test
    void givenAuthenticatedUserAndDefinedCommunity_whenGetCommunity_thenReturnCommunityDTO() {
        when(communityMemberService.isMember(anyLong(), anyLong())).thenReturn(Mono.just(true));

        User owner = userRepository.save(Instancio.of(User.class)
                        .set(field(User::getId), null)
                    .create())
                .block();

        assertThat(owner).isNotNull();

        Community community = communityRepository.save(Instancio.of(Community.class)
                        .set(field(Community::getId), null)
                        .set(field(Community::getOwnerId), owner.getId())
                    .create())
                .block();

        assertThat(community).isNotNull();

        client.get().uri("/communities/" + community.getId()).exchange()
                .expectStatus().isOk()
                .expectBody(Community.class) //TODO It really should be CommunityDTO
                .value((res) -> assertThat(res.getId()).isEqualTo(community.getId()));
    }
}
