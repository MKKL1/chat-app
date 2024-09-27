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

    public CommunityData saveComplexCommunity(GenericCommunityGenData genData) {
        //Using a lot of blocking code, but it's not really that important now
        //Feel free to add more functionality

        //1. generate owner user
        User owner = genData.getOwner() == null ? saveUser() : genData.getOwner();

        //2. generate user
        List<User> users = genData.getMembers() == null ? new ArrayList<>() : genData.getMembers();
        for (int i = 0; i < genData.getRandomMembers(); i++) {
            users.add(saveUser());
        }

        //3. Add owner to user list
        users.add(owner);

        //4. generate community
        Community community = communityRepository.save(Instancio.of(Community.class)
                .set(field(Community::getId), null)
                .set(field(Community::getOwnerId), owner.getId())
                .create()).block();

        assertThat(community).isNotNull();

        //5. generate members for community
        users.forEach(user -> {
            communityMemberRepository.save(CommunityMember.builder()
                    .communityId(community.getId())
                    .userId(user.getId())
                    .build()).block();
        });

        //6. generate roles

        //7. generate channels
        List<Channel> channels = genData.getChannels() == null ? new ArrayList<>() : genData.getChannels();
        for (int i = 0; i < genData.getRandomChannels(); i++) {
            channels.add(saveChannel(community.getId(), ChannelType.TEXT_CHANNEL));
        }

        //8. generate messages
        Random rand = new Random();
        final int usersCount = users.size();
        Map<Channel, List<Message>> messages = genData.getMessages() == null ? new HashMap<>() : genData.getMessages();
        channels.forEach(channel -> {
            List<Message> messagesForChannel = new ArrayList<>();
            for (int i = 0; i < genData.getRandomMessages(); i++) {
                User messageUser = users.get(rand.nextInt(usersCount));
                messagesForChannel.add(saveMessage(channel.getId(), messageUser.getId()));
            }
            messages.put(channel, messagesForChannel);
        });

        List<Message> messagesList = new ArrayList<>();
        messages.values().forEach(messagesList::addAll);
        return CommunityData.builder()
                .owner(owner)
                .members(users)
                .community(community)
                .channels(channels)
                .messages(messagesList)
                .build();
    }
}
