package com.szampchat.server.tools.populate;

import com.szampchat.server.channel.ChannelType;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.channel.repository.ChannelRepository;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.repository.CommunityMemberRepository;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.message.entity.Message;
import com.szampchat.server.message.repository.MessageRepository;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.entity.UserRole;
import com.szampchat.server.role.repository.RoleRepository;
import com.szampchat.server.role.repository.UserRoleRepository;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;

@AllArgsConstructor
@Component
public class TestDataGenerator {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final MessageRepository messageRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public User generateUser() {
        return Instancio.of(User.class)
                .set(field(User::getId), null)
                .create();
    }

    public User saveUser() {
        return userRepository.save(generateUser()).block();
    }

    public Channel generateChannel(Long communityId, ChannelType channelType) {
        return Instancio.of(Channel.class)
                .set(field(Channel::getId), null)
                .set(field(Channel::getCommunityId), communityId)
                .set(field(Channel::getType), channelType.getValue())
                .create();
    }

    public Channel saveChannel(Long communityId, ChannelType channelType) {
        return channelRepository.save(generateChannel(communityId, channelType)).block();
    }

    public Message generateMessage(Long channelId, Long userId) {
        return Instancio.of(Message.class)
                .set(field(Message::getId), null)
                .set(field(Message::getChannel), channelId)
                .set(field(Message::getUser), userId)
                .set(field(Message::getRespondsToMessage), null)
                .set(field(Message::getUpdated_at), null)
                .create();
    }

    public Message saveMessage(Long channelId, Long userId) {
        return messageRepository.save(generateMessage(channelId, userId)).block();
    }

    public Role generateRole(Long communityId) {
        return Instancio.of(Role.class)
                .set(field(Role::getId), null)
                .set(field(Role::getCommunity), communityId)
                .create();
    }

    public Role saveRole(Long communityId) {
        return roleRepository.save(generateRole(communityId)).block();
    }

    public CommunityData saveComplexCommunity(GenericCommunityGenData genData) {
        //Using a lot of blocking code, but it's not really that important now
        //Feel free to add more functionality

        //1. generate owner user
        User owner = genData.getOwner() == null ? saveUser() : genData.getOwner();
        assertThat(owner).isNotNull();

        //2. generate user
        List<User> users = genData.getMembers() == null ? new ArrayList<>() : genData.getMembers();
        for (int i = 0; i < genData.getRandomMembers(); i++) {
            User savedUser = saveUser();
            assertThat(savedUser).isNotNull();
            users.add(savedUser);
        }

        //3. Add owner to user list
        users.add(owner);

        //4. generate community
        Community community = communityRepository.save(Instancio.of(Community.class)
                .set(field(Community::getId), null)
                .set(field(Community::getOwnerId), owner.getId())
                .create()).block();

        assertThat(community).isNotNull();

        //5. save members for community
        for (User user : users) {
            CommunityMember communityMember = communityMemberRepository.save(CommunityMember.builder()
                    .communityId(community.getId())
                    .userId(user.getId())
                    .build()).block();
            assertThat(communityMember).isNotNull();
        }


        //6. generate roles
        List<Role> roles = genData.getRoles() == null ? new ArrayList<>() : genData.getRoles();
        for (int i = 0; i < genData.getRandomRoles(); i++) {
            Role savedRole = saveRole(community.getId());
            assertThat(savedRole).isNotNull();
            assertThat(savedRole.getCommunity()).isEqualTo(community.getId());
            roles.add(savedRole);
        }

        //7. select random roles for members
        for (User user : users) {
            //Select n random roles to give to user
            for (Role role : TestDataGenerator.pickNRandom(roles, genData.getRolesPerUser())) {
                UserRole savedUserRole = userRoleRepository.save(UserRole.builder()
                        .roleId(role.getId())
                        .userId(user.getId())
                        .build()).block();
                assertThat(savedUserRole).isNotNull();
            }
        }

        //8. generate channels
        List<Channel> channels = genData.getChannels() == null ? new ArrayList<>() : genData.getChannels();
        for (int i = 0; i < genData.getRandomChannels(); i++) {
            Channel savedChannel = saveChannel(community.getId(), ChannelType.TEXT_CHANNEL);
            assertThat(savedChannel).isNotNull();
            assertThat(savedChannel.getCommunityId()).isEqualTo(community.getId());
            channels.add(savedChannel);
        }

        //9. generate messages
        Random rand = new Random();
        final int usersCount = users.size();

        Map<Channel, List<Message>> messages = genData.getMessages() == null ? new HashMap<>() : genData.getMessages();
        //For each channel, generate messages
        for (Channel channel : channels) {
            List<Message> messagesForChannel = new ArrayList<>();
            for (int i = 0; i < genData.getRandomMessages(); i++) {
                //Get random user to be an author of message
                User messageUser = users.get(rand.nextInt(usersCount));
                Message message = saveMessage(channel.getId(), messageUser.getId());
                assertThat(message).isNotNull();
                messagesForChannel.add(message);
            }
            messages.put(channel, messagesForChannel);
        }


        //10. return response
        List<Message> messagesList = new ArrayList<>();
        messages.values().forEach(messagesList::addAll);
        return CommunityData.builder()
                .owner(owner)
                .members(users)
                .community(community)
                .roles(roles)
                .channels(channels)
                .messages(messagesList)
                .build();
    }

    public static <T> List<T> pickNRandom(List<T> lst, int n) {
        List<T> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);
        return n > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, n);
    }
}
