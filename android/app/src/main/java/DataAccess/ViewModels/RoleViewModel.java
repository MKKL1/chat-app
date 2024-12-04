package DataAccess.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Data.Databases.CommunityDB;
import Data.Models.Role;
import DataAccess.Repositories.RoleRepository;
import lombok.Getter;
import lombok.Setter;

public class RoleViewModel extends AndroidViewModel {
    private final RoleRepository roleRepository;
    @Getter
    private final LiveData<List<Role>> allRoles;

    public RoleViewModel(@NonNull Application application) {
        super(application);
        this.roleRepository = new RoleRepository(application);
        this.allRoles = roleRepository.getAllRoles();
    }
    public void addRole(Role role){
        roleRepository.addRole(role);
    }

    public LiveData<List<Role>> getRolesForCommunity(long communityId){
        return roleRepository.getRolesForCommunity(communityId);
    }

    public void deleteRole(Role role){
        roleRepository.deleteRole(role);
    }
    public void updateRole(Role role){
        roleRepository.updateRole(role);
    }
}
