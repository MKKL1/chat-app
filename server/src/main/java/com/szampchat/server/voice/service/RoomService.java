package com.szampchat.server.voice.service;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.channel.ChannelType;
import com.szampchat.server.user.UserService;
import com.szampchat.server.voice.dto.VoiceTokenResponse;
import com.szampchat.server.voice.exception.NotVoiceChannelException;
import com.szampchat.server.voice.repository.RedisRoomRepository;
import com.szampchat.server.livekit.dto.RoomDTO;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@AllArgsConstructor
@Service
public class RoomService {
    private final UserService userService;
    private final RedisRoomRepository roomRepository;
    private final ChannelService channelService;

    public Flux<RoomDTO> getRoomForChannelBulk(Collection<Long> channelIds) {
        return roomRepository.findRooms(channelIds.stream()
                        .map(Object::toString)
                        .toList());
    }

    public Mono<AccessToken> getTokenForChannel(Long channelId, Long userId) {
        return channelService.getChannel(channelId)
                .filter(channelDTO -> channelDTO.getType() == ChannelType.VOICE_CHANNEL)
                .switchIfEmpty(Mono.error(new NotVoiceChannelException(channelId)))
                .flatMap(_ -> userService.findUserDTO(userId)
                        .map(user -> {
                            AccessToken token = new AccessToken("devkey", "secret");//TODO

                            token.setName(user.getUsername());
                            token.setIdentity(userId.toString());
                            token.addGrants(new RoomJoin(true), new RoomName(channelId.toString()));

                            return token;
                        })
                );

    }

    public Mono<VoiceTokenResponse> getVoiceTokenResponse(Long channelId, Long userId) {
        return getTokenForChannel(channelId, userId)
                .map(token -> new VoiceTokenResponse(token.toJwt()));
    }


}
