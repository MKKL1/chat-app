package DataAccess.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Data.Models.Community;
import DataAccess.Repositories.CommunityRepository;
import lombok.Getter;

/**
 * All communities are mapped from CommunityDTO to CommunityModel before any operation
 */
public class CommunityViewModel extends AndroidViewModel {
    private final CommunityRepository communityRepository;
    @Getter
    private final LiveData<List<Community>> allCommunities;

    public CommunityViewModel(@NonNull Application application) {
        super(application);
        this.communityRepository = new CommunityRepository(application);
        this.allCommunities = communityRepository.getCommunities();
    }

    public void addCommunity(Community community){
        communityRepository.addCommunity(community);
    }
    public void updateCommunity(Community community){
        communityRepository.updateCommunity(community);
    }
    public void deleteCommunity(Community community){
        communityRepository.deleteCommunity(community);
    }
}
