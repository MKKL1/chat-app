package com.szampchat.server.user;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.channel.repository.ChannelRepository;
import com.szampchat.server.channel.ChannelType;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.message.base.entity.Message;
import com.szampchat.server.message.base.repository.MessageRepository;
import com.szampchat.server.message.reaction.entity.Reaction;
import com.szampchat.server.message.reaction.repository.ReactionRepository;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.repository.RoleRepository;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class UserRepositoryTests {

    //To nie test a generator więc usuwam to
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
    @Autowired
    private ReactionRepository reactionRepository;

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
                    Message savedMessage = messageRepository.save(message);

                    for(int l = 0; l < 3; l++) {
                        Reaction reaction = Reaction.builder()
                                .emoji('a')
                                .channel(channel)
                                .message(savedMessage) //why?
                                .user(memberList.get(rand.nextInt(memberList.size())))
                                .build();
                        reactionRepository.save(reaction);
                    }
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
