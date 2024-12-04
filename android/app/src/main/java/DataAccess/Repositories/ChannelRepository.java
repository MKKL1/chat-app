package DataAccess.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import Data.DTO.ChannelResponseDTO;
import Data.DTO.ChannelType;
import Data.DTO.ChannelDTO;
import Data.Databases.CommunityDB;
import Data.Models.Channel;
import Data.Models.ChannelRole;
import DataAccess.DAO.ChannelDAO;
import lombok.Getter;

public class ChannelRepository {
    private ChannelDAO dao;
    @Getter
    private LiveData<List<Channel>> allChannels;

    public ChannelRepository(Application application) {
        CommunityDB communityDB = CommunityDB.getDataBase(application);
        dao = communityDB.channelDAO();
        allChannels = dao.getAllChannels();
    }

    /**
     * Mapping ChannelDTO to Channel for Room Database
     * @param channelResponseDTO object to mapping
     * @return new Channel object
     */
    public Channel mapChannel(ChannelResponseDTO channelResponseDTO){
        ChannelDTO channelDTO = channelResponseDTO.getChannel();
        List<ChannelRole> overwrites = new ArrayList<>();
        for(ChannelRole role : channelResponseDTO.getOverwrites()){
            overwrites.add(new ChannelRole(role.getChannelOverwrites(), role.getRoleId()));
        }
        return new Channel(
                channelDTO.getId(),
                channelDTO.getName(),
                channelDTO.getCommunityId(),
//                ChannelType from server is 0-1
                (channelDTO.getType()==1) ? ChannelType.VOICE_CHANNEL : ChannelType.TEXT_CHANNEL,
                channelResponseDTO.getParticipants(),
                overwrites
        );
    }
    public void addChannel(Channel channel){
        CommunityDB.databaseWriteExecutor.execute(() -> {
            dao.addChannel(channel);
        });
    }
    public LiveData<List<Channel>> getChannelsForCommunity(long communityId){
        return dao.getChannelsForCommunity(communityId);
    }

    public void deleteChannel(Channel channel){
        CommunityDB.databaseWriteExecutor.execute(() -> {
            dao.deleteChannel(channel);
        });
    }
    public void updateChannel(Channel channel){
        CommunityDB.databaseWriteExecutor.execute(() -> {
            dao.updateChannel(channel);
        });
    }
}
