package com.szampchat.server.voice;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.channel.ChannelType;
import com.szampchat.server.user.UserService;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.voice.dto.VoiceTokenResponse;
import com.szampchat.server.voice.exception.NotVoiceChannelException;
import com.szampchat.server.voice.repository.RoomRepository;
import com.szampchat.server.voice.livekit.dto.RoomDTO;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import io.livekit.server.RoomServiceClient;
import jakarta.annotation.PostConstruct;
import livekit.LivekitModels;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

import static com.szampchat.server.voice.RetrofitToReactor.toMono;

@AllArgsConstructor
@Service
public class RoomService {
    private final UserService userService;
    private final RoomServiceClient roomServiceClient;
    private final RoomRepository roomRepository;
    private final ChannelService channelService;

    public Flux<RoomDTO> getRoomForChannelBulk(Collection<Long> channelIds) {
        return roomRepository.findRooms(channelIds.stream()
                        .map(Object::toString)
                        .toList());
    }

    public Mono<AccessToken> getTokenForChannel(Long channelId, Long userId) {
        return channelService.getChannelDTO(channelId)
                .filter(channelDTO -> channelDTO.getType() == ChannelType.VOICE_CHANNEL)
                .switchIfEmpty(Mono.error(new NotVoiceChannelException()))
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
                .map(token -> new VoiceTokenResponse(token.toJwt(), "ws://localhost:7880")); //TODO
    }

    RoomDTO toDTO(LivekitModels.Room room) {
        return RoomDTO.builder()
                .sid(room.getSid())
                .name(room.getName())
                .build();
    }
}
