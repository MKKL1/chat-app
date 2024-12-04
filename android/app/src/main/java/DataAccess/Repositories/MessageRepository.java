package DataAccess.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import Data.Databases.CommunityDB;
import Data.Models.Message;
import DataAccess.DAO.MessageDAO;
import lombok.Getter;

public class MessageRepository {
    private MessageDAO dao;

    public MessageRepository(Application application){
        CommunityDB communityDB = CommunityDB.getDataBase(application);
        dao = communityDB.messageDAO();
    }
    public void addMessage(Message message){
        CommunityDB.databaseWriteExecutor.execute(()->{
            dao.addMessage(message);
        });
    }

    public LiveData<List<Message>> getAllMessagesFromChannel(long channelId){
        return dao.getAllMessagesFromChannel(channelId);
    }
    public LiveData<List<Message>> getAllMessagesFromUser(long userId){
        return dao.getAllMessagesFromUser(userId);
    }
    public LiveData<Message> getMessageById(long messageId){
        return dao.getMessageById(messageId);
    }
    public void updateMessage(Message message){
        CommunityDB.databaseWriteExecutor.execute(()->{
            dao.updateMessage(message);
        });
    }
    public void deleteMessage(Message message){
        CommunityDB.databaseWriteExecutor.execute(()->{
            dao.deleteMessage(message);
        });
    }
}
