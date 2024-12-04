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

import Data.Models.Channel;

@Dao
public interface ChannelDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addChannel(Channel channel);
    @Update
    void updateChannel(Channel channel);
    @Transaction
    @Query("SELECT * FROM channels")
    LiveData<List<Channel>> getAllChannels();

    @Transaction
    @Query("SELECT * FROM channels WHERE `Community Id` = :communityId")
    LiveData<List<Channel>> getChannelsForCommunity(long communityId);

    @Transaction
    @Query("SELECT * FROM channels WHERE `Channel Id` = :channelId")
    LiveData<Channel> getChannelById(long channelId);

    @Delete
    void deleteChannel(Channel channel);
}
