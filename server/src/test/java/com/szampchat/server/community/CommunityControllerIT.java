package com.szampchat.server.community;

import com.szampchat.server.PostgresTestContainer;
import com.szampchat.server.TestSecurityConfiguration;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.dto.CreateInvitationDTO;
import com.szampchat.server.community.dto.InvitationResponseDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.repository.CommunityRepository;
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
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@Slf4j
@Testcontainers
@AutoConfigureWebTestClient(timeout = "3600000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfiguration.class)
@ActiveProfiles(value = "test")
public class CommunityControllerIT implements PostgresTestContainer {


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

    @WithMockCustomUser
    @Test
    void givenDefinedCommunity_whenGetCommunity_thenReturnCommunityDTO() {
        CommunityData communityData = testDataGenerator.saveComplexCommunity(GenericCommunityGenData.builder().build());

        client.get().uri("/communities/" + communityData.getCommunity().getId()).exchange()
                .expectStatus().isOk()
                .expectBody(CommunityDTO.class)
                .value((res) -> assertThat(res.getId()).isEqualTo(communityData.getCommunity().getId()));
    }
//
//    void givenDefinedCommunity_whenGetFullCommunityInfo_thenReturnFullCommunityInfoDTO() {
//        //TODO
//    }
//
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
