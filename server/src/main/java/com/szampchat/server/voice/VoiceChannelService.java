package com.szampchat.server.voice;

import com.szampchat.server.user.UserService;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.voice.dto.VoiceTokenResponse;
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

@AllArgsConstructor
@Service
public class VoiceChannelService {
    private UserService userService;
    private RoomServiceClient roomServiceClient;
    private final Scheduler scheduler = Schedulers.boundedElastic();

    private Mono<LivekitModels.Room> createRoom(Long channelId) {
        return Mono.fromCallable(() -> {
            Call<LivekitModels.Room> call = roomServiceClient.createRoom(channelId.toString());
            Response<LivekitModels.Room> response = call.execute();
            return response.body();
        })
        .publishOn(scheduler);
    }

    public Mono<AccessToken> getTokenForChannel(Long channelId, Long userId) {
        return Mono.zip(createRoom(channelId), userService.findUserDTO(userId))
                .map(tuple -> {
                    LivekitModels.Room room = tuple.getT1();
                    UserDTO user = tuple.getT2();

                    AccessToken token = new AccessToken("devkey", "secret");

                    token.setName(user.getUsername());
                    token.setIdentity(userId.toString());
                    token.addGrants(new RoomJoin(true), new RoomName(room.getName()));

                    return token;
                });

    }
}
