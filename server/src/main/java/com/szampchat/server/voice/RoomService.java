package com.szampchat.server.voice;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.channel.ChannelType;
import com.szampchat.server.user.UserService;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.voice.dto.VoiceTokenResponse;
import com.szampchat.server.voice.exception.NotVoiceChannelException;
import com.szampchat.server.voice.livekit.LivekitRepository;
import com.szampchat.server.voice.livekit.dto.RoomDTO;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import io.livekit.server.RoomServiceClient;
import livekit.LivekitModels;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.szampchat.server.voice.RetrofitToReactor.toMono;

@AllArgsConstructor
@Service
public class RoomService {
    private final UserService userService;
    private final RoomServiceClient roomServiceClient;
    private final LivekitRepository livekitRepository;
    private final ChannelService channelService;

    private Mono<RoomDTO> createOrGetRoom(Long channelId) {
        return livekitRepository.findRoom(channelId.toString())
                .switchIfEmpty(toMono(roomServiceClient.createRoom(channelId.toString()))
                                .map(this::toDTO)
                                .flatMap(room -> livekitRepository.addRoom(channelId.toString(), room)
                                        .then(Mono.just(room))
                                )
                );
    }

    public Mono<AccessToken> getTokenForChannel(Long channelId, Long userId) {
        return channelService.getChannelDTO(channelId)
                .filter(channelDTO -> channelDTO.getType() == ChannelType.VOICE_CHANNEL)
                .switchIfEmpty(Mono.error(new NotVoiceChannelException()))
                .flatMap(_ -> Mono.zip(createOrGetRoom(channelId), userService.findUserDTO(userId))
                        .map(tuple -> {
                            RoomDTO room = tuple.getT1();
                            UserDTO user = tuple.getT2();

                            AccessToken token = new AccessToken("devkey", "secret");//TODO

                            token.setName(user.getUsername());
                            token.setIdentity(userId.toString());
                            token.addGrants(new RoomJoin(true), new RoomName(room.getName()));

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
