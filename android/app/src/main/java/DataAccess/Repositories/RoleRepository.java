package DataAccess.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import Data.Databases.CommunityDB;
import Data.Models.Role;
import DataAccess.DAO.RoleDAO;
import lombok.Getter;

public class RoleRepository {
    private RoleDAO dao;
    @Getter
    private LiveData<List<Role>> allRoles;

    public RoleRepository(Application application){
        CommunityDB communityDB = CommunityDB.getDataBase(application);
        dao = communityDB.roleDAO();
        allRoles = dao.getAllRoles();
    }

    public void addRole(Role role){
        CommunityDB.databaseWriteExecutor.execute(()->{
            dao.addRole(role);
        });
    }

    public LiveData<List<Role>> getRolesForCommunity(long communityId){
        return dao.getRolesForCommunity(communityId);
    }

    public void deleteRole(Role role){
        CommunityDB.databaseWriteExecutor.execute(()->{
            dao.deleteRole(role);
        });
    }
    public void updateRole(Role role){
        CommunityDB.databaseWriteExecutor.execute(()->{
            dao.updateRole(role);
        });
    }
}
