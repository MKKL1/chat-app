package DataAccess.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Data.DTO.ChannelResponseDTO;
import Data.Models.Channel;
import DataAccess.Repositories.ChannelRepository;
import lombok.Getter;
import lombok.Setter;

public class ChannelViewModel extends AndroidViewModel {
    private final ChannelRepository channelRepository;
    @Getter
    private final LiveData<List<Channel>> allChannels;

    public ChannelViewModel(@NonNull Application application) {
        super(application);
        this.channelRepository = new ChannelRepository(application);
        this.allChannels = channelRepository.getAllChannels();
    }
    public LiveData<List<Channel>> getChannelsFromCommunity(long id){
        return channelRepository.getChannelsForCommunity(id);
    }

    public void addChannel(ChannelResponseDTO channelResponseDTO){
        channelRepository.addChannel(channelRepository.mapChannel(channelResponseDTO));
    }
    public void updateChannel(ChannelResponseDTO channelResponseDTO){
        channelRepository.updateChannel(channelRepository.mapChannel(channelResponseDTO));
    }
    public void deleteChannel(ChannelResponseDTO channelResponseDTO){
        channelRepository.deleteChannel(channelRepository.mapChannel(channelResponseDTO));
    }

    public void addChannel(Channel channel){
        channelRepository.addChannel(channel);
    }
    public void updateChannel(Channel channel){
        channelRepository.updateChannel(channel);
    }
    public void deleteChannel(Channel channel){
        channelRepository.deleteChannel(channel);
    }
}
