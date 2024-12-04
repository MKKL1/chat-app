package Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.szampchat.R;

import java.util.List;

import Data.Models.Message;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    Activity activity;
    List<Message> messagesList;

    long userId;

    private LayoutInflater layoutInflater;
    private MessageAdapterListener messageAdapterListener;

    public MessageAdapter(Activity activity) {
        this.activity = activity;
        this.layoutInflater = LayoutInflater.from(activity);
        this.messagesList = null;

        SharedPreferences sharedPreferences = activity.getSharedPreferences("app_prefs", MODE_PRIVATE);
        this.userId = sharedPreferences.getLong("userId", 0);

        try {
            messageAdapterListener = (MessageAdapterListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage() + " - must implement MessageAdapter.OnItemClickListener interface");
        }
    }
    public interface MessageAdapterListener {
        void onItemClickListener(Message message);
        void onItemLongClickListener(Message message);
    }

    public void setMessagesList(List<Message> messagesList) {
        this.messagesList = messagesList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (messagesList != null) ? messagesList.size() : 0;
    }
    //        TODO poprawic logige xd sprawdzania czy wiadomosc wyslana przez uzytkownika zalogowanego do aplikacji.
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) messagesList.get(position);
        if (message.getUserId() == userId) return VIEW_TYPE_MESSAGE_SENT;
        else return VIEW_TYPE_MESSAGE_RECEIVED;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
//        Setup message layout depending on type
        if (viewType == VIEW_TYPE_MESSAGE_SENT){
            view = layoutInflater.inflate(R.layout.item_list_message_sent, parent, false);
            return new SentMessageHolder(view);
        }
        else {
            view = layoutInflater.inflate(R.layout.item_list_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messagesList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
        holder.itemView.setOnLongClickListener(v->{
            messageAdapterListener.onItemLongClickListener(message);
            return true;
        });
    }


//    ViewHolder for messages sent by user in app
    public class SentMessageHolder extends RecyclerView.ViewHolder {

        TextView messageText, messageTimestamp;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.messageText);
//            messageTimestamp = (TextView) itemView.findViewById(R.id.messageTimestamp);
            itemView.setTag(this);
        }
        public void bind(Message message) {
            messageText.setText(message.getText());
//            messageTimestamp.setText(message.getUpdatedAt().toString());
        }
    }

//    ViewHolder for messages received by user from server
    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView messageText, messageUser, messageTimestamp;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            messageUser = itemView.findViewById(R.id.messageUser);
            messageTimestamp = itemView.findViewById(R.id.messageTimestamp);
            itemView.setTag(this);
        }
        public void bind(Message message) {
            messageText.setText(message.getText());
//            messageTimestamp.setText(message.getUpdatedAt().toString());
            // Domyślna wartość, jeśli użytkownik nie istnieje
            messageUser.setText(String.valueOf(message.getUserId()));
        }
    }

//    ViewHolder for messages sent by SYSTEM (new user joined etc.)
//    public class SystemMessageHolder extends RecyclerView.ViewHolder {
//
//        TextView systemMessage;
//
//        public SystemMessageHolder(@NonNull View itemView) {
//            super(itemView);
//            systemMessage = itemView.findViewById(R.id.systemMessageText);
//        }
//        public void bind(Message message){
//            systemMessage.setText(message.getText());
//        }
//    }
}
