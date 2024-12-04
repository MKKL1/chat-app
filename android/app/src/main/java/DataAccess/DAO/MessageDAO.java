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

import Data.Models.Message;

@Dao
public interface MessageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addMessage(Message message);

    @Update
    void updateMessage(Message message);

    @Delete
    void deleteMessage(Message message);

    @Transaction
    @Query("SELECT * FROM messages WHERE id = :messageId")
    LiveData<Message> getMessageById(long messageId);

    @Transaction
    @Query("SELECT * FROM messages WHERE channelId = :channelId")
    LiveData<List<Message>> getAllMessagesFromChannel(long channelId);

    @Transaction
    @Query("SELECT * FROM messages WHERE userId = :userId")
    LiveData<List<Message>> getAllMessagesFromUser(long userId);
}
