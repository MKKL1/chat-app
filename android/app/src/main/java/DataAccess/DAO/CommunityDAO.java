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

import Data.Models.Community;

@Dao
public interface CommunityDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCommunity(Community community);
    @Update
    void updateCommunity(Community community);
    @Transaction
    @Query("SELECT * FROM communities")
    LiveData<List<Community>> getAllCommunities();
    @Delete
    void deleteCommunity(Community community);
}
