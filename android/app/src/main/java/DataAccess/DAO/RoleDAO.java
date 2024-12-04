package DataAccess.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import Data.Models.Role;

@Dao
public interface RoleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addRole(Role role);

    @Update
    void updateRole(Role role);

    @Transaction
    @Query("SELECT * FROM roles")
    LiveData<List<Role>> getAllRoles();

    @Transaction
    @Query("SELECT * FROM roles WHERE `Community ID` = :communityId")
    LiveData<List<Role>> getRolesForCommunity(long communityId);

    @Transaction
    @Query("SELECT * FROM roles WHERE `Role ID` = :roleId")
    LiveData<Role> getRoleById(long roleId);

    @Delete
    void deleteRole(Role role);
}
