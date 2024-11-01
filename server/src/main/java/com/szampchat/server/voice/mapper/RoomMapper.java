package com.szampchat.server.voice.mapper;

import com.szampchat.server.livekit.dto.RoomDTO;
import livekit.LivekitModels;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RoomMapper {

    public String channelIdToRoomId(Long channelId) {
        return channelId.toString();
    }

    public Long roomIdToChannelId(String roomId) {
        try {
            return Long.parseLong(roomId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid roomId (" + roomId + ")");
        }
    }

    public RoomDTO toDTO(LivekitModels.Room room) {
        return RoomDTO.builder()
                .sid(room.getSid())
                .name(room.getName())
                .build();
    }
}
