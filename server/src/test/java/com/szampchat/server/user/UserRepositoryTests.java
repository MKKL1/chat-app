package com.szampchat.server.user;
import com.szampchat.server.channel.Channel;
import com.szampchat.server.channel.ChannelRepository;
import com.szampchat.server.channel.ChannelType;
import com.szampchat.server.community.Community;
import com.szampchat.server.community.CommunityRepository;
import com.szampchat.server.message.base.Message;
import com.szampchat.server.message.base.MessageId;
import com.szampchat.server.message.base.MessageRepository;
import com.szampchat.server.role.Role;
import com.szampchat.server.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void test_save_user() {
        Random rand = new Random();
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

        List<Community> communities = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Collections.shuffle(users);
            Set<User> members = users.stream().limit(5).collect(Collectors.toSet());
            Community community = Community.builder()
                    .name("community")
                    .members(members)
                    .build();
            communityRepository.save(community);
            communities.add(community);

            List<User> memberList = new ArrayList<>(members.stream().toList());

            for(int j = 0; j < 5; j++) {
                Channel channel = Channel.builder()
                        .type(ChannelType.TEXT_CHANNEL)
                        .community(community)
                        .name(STR."Channel \{j}")
                        .build();
                channelRepository.save(channel);

                for(int k = 0; k < 10; k++) {
                    Message message = Message.builder()
                            .text(STR."Message \{k}")
                            .user(memberList.get(rand.nextInt(memberList.size())))
                            .channel(channel)
                            .build();
                    messageRepository.save(message);
                }
            }

            for(int j = 0; j < 3; j++) {

                Collections.shuffle(memberList);
                Role role = Role.builder()
                        .community(community)
                        .users(memberList.stream().limit(3).collect(Collectors.toSet()))
                        .name(STR."Role\{j}")
                        .permission(15L)
                        .build();
                roleRepository.save(role);
            }
        }
    }
}
