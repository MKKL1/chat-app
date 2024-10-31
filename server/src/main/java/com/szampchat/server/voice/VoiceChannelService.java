package com.szampchat.server.voice;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.channel.ChannelType;
import com.szampchat.server.user.UserService;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.voice.dto.VoiceTokenResponse;
import com.szampchat.server.voice.exception.NotVoiceChannelException;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import io.livekit.server.RoomServiceClient;
import livekit.LivekitModels;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.szampchat.server.voice.RetrofitToReactor.toMono;

@AllArgsConstructor
@Service
public class VoiceChannelService {
    private final UserService userService;
    private final RoomServiceClient roomServiceClient;
    private final LivekitRepository livekitRepository;
    private final ChannelService channelService;

    private Mono<LivekitModels.Room> createOrGetRoom(Long channelId) {
        return livekitRepository.findRoom(channelId)
                .switchIfEmpty(toMono(roomServiceClient.createRoom(channelId.toString()))
                        .flatMap(room ->
                                livekitRepository.addRoom(channelId, room)
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
                            LivekitModels.Room room = tuple.getT1();
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
}
