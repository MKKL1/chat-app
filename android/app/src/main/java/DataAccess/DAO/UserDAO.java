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

import Data.Models.User;
import lombok.Setter;

@Dao
public interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUser(User user);

    @Update
    void updateUser(User user);

    @Transaction
    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllUsers();

    @Transaction
    @Query("SELECT Username FROM users WHERE userID = :userId")
    String getUserName(long userId);

    @Transaction
    @Query("SELECT * FROM users WHERE `Communities List` LIKE :communityId")
    LiveData<List<User>> getUsersForCommunity(long communityId);

    @Transaction
    @Query("SELECT * FROM users WHERE userID = :userId")
    LiveData<User> getUserById(long userId);

    @Delete
    void deleteUser(User user);
}
