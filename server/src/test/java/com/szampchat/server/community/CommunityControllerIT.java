package com.szampchat.server.community;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

//@Slf4j
//@Testcontainers
//@AutoConfigureWebTestClient(timeout = "3600000")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext
//@ExtendWith(MockitoExtension.class)
public class CommunityControllerIT
//        implements PostgresTestContainer
{

    //For mocking isMember
//    @MockBean
//    private AuthorizationManagerFactory authorizationManagerFactory;
//
//    @Autowired
//    private CommunityRepository communityRepository;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    protected WebTestClient client;
//
//    @Autowired
//    private TestDataGenerator testDataGenerator;
//
//
//    @BeforeAll
//    static void beforeAll() {
//        postgres.start();
//    }
//
//    @AfterAll
//    static void afterAll() {
//        postgres.stop();
//    }
//
//    @Test
//    @Override
//    public void connectionEstablished() {
//        PostgresTestContainer.super.connectionEstablished();
//    }

////    @Transactional
//    @WithMockCustomUser
//    @Test
//    void givenDefinedCommunity_whenGetCommunity_thenReturnCommunityDTO() {
////        when(authorizationManagerFactory.create(any(), any(), any())).thenReturn((_, _) -> Mono.just(new AuthorizationDecision(true)));
////        when(authorizationManagerFactory.create(any())).thenReturn((_, _) -> Mono.just(new AuthorizationDecision(true)));
//
////        ReactiveAuthorizationManager<AuthorizationContext> mockAuthManager = mock(ReactiveAuthorizationManager.class);
//        when(authorizationManagerFactory.check(any(), any())).thenReturn(Mono.just(new AuthorizationDecision(true)));
//
//        when(authorizationManagerFactory.create(any(), any(), any()))
//                .thenReturn(authorizationManagerFactory);
//
//        when(authorizationManagerFactory.create(any(AuthorizationMethod.class)))
//                .thenReturn(authorizationManagerFactory);
//
//        User owner = userRepository.save(Instancio.of(User.class)
//                        .set(field(User::getId), null)
//                    .create())
//                .block();
//
//        assertThat(owner).isNotNull();
//
//        Community community = communityRepository.save(Instancio.of(Community.class)
//                        .set(field(Community::getId), null)
//                        .set(field(Community::getOwnerId), owner.getId())
//                    .create())
//                .block();
//
//        assertThat(community).isNotNull();
//
//        client.get().uri("/communities/" + community.getId()).exchange()
//                .expectStatus().isOk()
//                .expectBody(Community.class) //TODO It really should be CommunityDTO
//                .value((res) -> assertThat(res.getId()).isEqualTo(community.getId()));
//    }
//
//    void givenDefinedCommunity_whenGetFullCommunityInfo_thenReturnFullCommunityInfoDTO() {
//        //TODO
//    }
//
//    @WithMockCustomUser
//    @Test
//    void givenDefinedCommunity_whenInviteToCommunity_thenReturnInvitationResponseDTO() {
////        when(communityService.isOwner(anyLong())).thenReturn(Mono.just(true));
//
//        Pattern linkPattern = Pattern.compile("^community/(\\d+)/join/(\\d+)$");
//
//
//        CommunityData communityData = testDataGenerator.saveComplexCommunity(GenericCommunityGenData.builder().build()); //Minimal setup
//
//        client.post().uri("/communities/" + communityData.getCommunity().getId() + "/invite")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(CreateInvitationDTO.builder()
//                        .days(5)
//                        .build())
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(InvitationResponseDTO.class)
//                .value((res) -> assertThat(res.getLink()).isNotEmpty().containsPattern(linkPattern));
//    }
}
