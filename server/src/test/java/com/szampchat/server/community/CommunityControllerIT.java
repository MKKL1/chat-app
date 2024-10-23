package com.szampchat.server.community;

import com.szampchat.server.PostgresTestContainer;
import com.szampchat.server.RabbitMQTestContainer;
import com.szampchat.server.auth.AuthorizationManagerFactory;
import com.szampchat.server.auth.AuthorizationMethod;
import com.szampchat.server.community.dto.CreateInvitationDTO;
import com.szampchat.server.community.dto.InvitationResponseDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.tools.WithMockCustomUser;
import com.szampchat.server.tools.populate.CommunityData;
import com.szampchat.server.tools.populate.GenericCommunityGenData;
import com.szampchat.server.tools.populate.TestDataGenerator;
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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ServerWebExchange;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@Testcontainers
@AutoConfigureWebTestClient(timeout = "3600000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ExtendWith(MockitoExtension.class)
public class CommunityControllerIT implements PostgresTestContainer {

    //For mocking isMember
    @MockBean
    private AuthorizationManagerFactory authorizationManagerFactory;

    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    protected WebTestClient client;

    @Autowired
    private TestDataGenerator testDataGenerator;


    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    @Override
    public void connectionEstablished() {
        PostgresTestContainer.super.connectionEstablished();
    }

//    @Transactional
    @WithMockCustomUser
    @Test
    void givenDefinedCommunity_whenGetCommunity_thenReturnCommunityDTO() {
//        when(authorizationManagerFactory.create(any(), any(), any())).thenReturn((_, _) -> Mono.just(new AuthorizationDecision(true)));
//        when(authorizationManagerFactory.create(any())).thenReturn((_, _) -> Mono.just(new AuthorizationDecision(true)));

//        ReactiveAuthorizationManager<AuthorizationContext> mockAuthManager = mock(ReactiveAuthorizationManager.class);
        when(authorizationManagerFactory.check(any(), any())).thenReturn(Mono.just(new AuthorizationDecision(true)));

        when(authorizationManagerFactory.create(any(), any(), any()))
                .thenReturn(authorizationManagerFactory);

        when(authorizationManagerFactory.create(any(AuthorizationMethod.class)))
                .thenReturn(authorizationManagerFactory);

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

    void givenDefinedCommunity_whenGetFullCommunityInfo_thenReturnFullCommunityInfoDTO() {
        //TODO
    }

    @WithMockCustomUser
    @Test
    void givenDefinedCommunity_whenInviteToCommunity_thenReturnInvitationResponseDTO() {
//        when(communityService.isOwner(anyLong())).thenReturn(Mono.just(true));

        Pattern linkPattern = Pattern.compile("^community/(\\d+)/join/(\\d+)$");


        CommunityData communityData = testDataGenerator.saveComplexCommunity(GenericCommunityGenData.builder().build()); //Minimal setup

        client.post().uri("/communities/" + communityData.getCommunity().getId() + "/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(CreateInvitationDTO.builder()
                        .days(5)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(InvitationResponseDTO.class)
                .value((res) -> assertThat(res.getLink()).isNotEmpty().containsPattern(linkPattern));
    }
}
