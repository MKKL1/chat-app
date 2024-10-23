package com.szampchat.server.channel;

import com.szampchat.server.channel.dto.ChannelCreateDTO;
import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.dto.ChannelRolesDTO;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.channel.exception.ChannelAlreadyExistsException;
import com.szampchat.server.channel.exception.ChannelNotFoundException;
import com.szampchat.server.channel.repository.ChannelRepository;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.permission.data.PermissionOverwrites;
import com.szampchat.server.role.ChannelRoleService;
import com.szampchat.server.role.dto.ChannelRoleOverwriteDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service("channelService")
@AllArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final CommunityMemberService communityMemberService;
    private ChannelRoleService channelRoleService;
    private final ModelMapper modelMapper;

    //TODO cache it (this method will be called on most api operations)
    public Mono<Boolean> isParticipant(Long channelId, Long userId) {
        return getChannel(channelId)
//                .doFirst(() -> System.out.println(channelId + " " + userId))
                .flatMap(channel -> communityMemberService.isMember(channel.getCommunityId(), userId));
    }

    public Mono<Channel> getChannel(Long channelId) {
        return channelRepository.findById(channelId)
                .switchIfEmpty(Mono.error(new ChannelNotFoundException()));
    }


    public Flux<ChannelDTO> getCommunityChannels(Long communityId) {
        return channelRepository.findChannelsByCommunityId(communityId)
                .map(this::toDTO);
    }

    public Flux<ChannelRolesDTO> getCommunityChannelsWithRoles(Long communityId) {
        return getCommunityChannels(communityId)
                .flatMap(channelDTO ->
                        //For each channel, get it's overwrites
                        //TODO get in bulk
                        channelRoleService.getChannelOverwrites(channelDTO.getId())
                        .collectList()
                        .map(list -> {
                            Map<Long, PermissionOverwrites> overwritesMap = new HashMap<>();
                            list.forEach(x -> overwritesMap.put(x.getRoleId(), x.getOverwrites()));
                            return overwritesMap;
                        })
                        .map(map -> new ChannelRolesDTO(channelDTO, map))
                );
    }

    public Mono<Channel> createChannel(ChannelCreateDTO channel) {
        //TODO check if channel exists in different way
        return channelRepository.doesChannelExist(channel.getName(), channel.getCommunityId())
            .flatMap(existingChannel -> existingChannel ?
                Mono.error(new ChannelAlreadyExistsException()) :
                channelRepository.save(
                    Channel.builder()
                        .name(channel.getName())
                        .communityId(channel.getCommunityId())
                        .type(channel.getType())
                        .build()
                    )
                );
    }

    public Mono<Channel> editChannel(Long id, Channel channel){
        return channelRepository.findById(id)
            .flatMap(existingChannel -> {
               existingChannel = channel;
               return channelRepository.save(existingChannel);
            });
    }

    // issue with constraints
    public Mono<Void> deleteChannel(Long id){
        return channelRepository.deleteById(id);
    }


    public ChannelDTO toDTO(Channel channel) {
        return modelMapper.map(channel, ChannelDTO.class);
    }
}
