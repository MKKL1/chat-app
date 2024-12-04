package DataAccess.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Data.Databases.CommunityDB;
import Data.Models.Message;
import DataAccess.Repositories.CommunityRepository;
import DataAccess.Repositories.MessageRepository;

public class MessageViewModel extends AndroidViewModel {
    private final MessageRepository messageRepository;

    public MessageViewModel(@NonNull Application application) {
        super(application);
        this.messageRepository = new MessageRepository(application);
    }

    public void addMessage(Message message){
        messageRepository.addMessage(message);
    }
    public LiveData<List<Message>> getAllMessagesFromChannel(long channelId){
        return messageRepository.getAllMessagesFromChannel(channelId);
    }
    public LiveData<List<Message>> getAllMessagesFromUser(long userId){
        return messageRepository.getAllMessagesFromUser(userId);
    }
    public LiveData<Message> getMessageById(long messageId){
        return messageRepository.getMessageById(messageId);
    }
    public void updateMessage(Message message){
        messageRepository.updateMessage(message);
    }
    public void deleteMessage(Message message){
        messageRepository.deleteMessage(message);
    }

}
