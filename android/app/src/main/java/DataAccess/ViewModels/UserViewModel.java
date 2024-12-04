package DataAccess.ViewModels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import Data.DTO.MemberDTO;
import Data.Models.User;
import DataAccess.Repositories.UserRepository;
import lombok.Getter;
import lombok.Setter;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    @Getter
    private final LiveData<List<User>> allUsers;
    @Getter
    private LiveData<List<User>> communityUsers;

    public UserViewModel(Application application) {
        super(application);
        this.userRepository = new UserRepository(application);
        this.allUsers = userRepository.getAllUsers();
    }
    public User mapUser(MemberDTO memberDTO){
        return new User(
                memberDTO.getUser().getUserId(),
                new ArrayList<>(),
                memberDTO.getUser().getUsername(),
                memberDTO.getUser().getImageUrl(),
                memberDTO.getUser().getDescription(),
                memberDTO.getRoles()
        );
    }

//    public LiveData<List<User>> getUsersForCommunity(long communityId){
//        return userRepository.getUsersForCommunity(communityId);
//    }

    public void addUser(User user) {
        userRepository.addUser(user);
    }
    public void addUser(MemberDTO memberDTO) {
        userRepository.addUser(mapUser(memberDTO));
    }
    public LiveData<User> getUserById(long userId){
        return userRepository.getUserById(userId);
    }
    public void updateUser(User user){
        userRepository.updateUser(user);
    }
    public void deleteUser(User user){
        userRepository.deleteUser(user);
    }
}
