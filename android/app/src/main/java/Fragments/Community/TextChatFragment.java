package Fragments.Community;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.szampchat.R;

import java.util.ArrayList;
import java.util.List;

import Adapters.MessageAdapter;
import Config.env;
import Data.Models.Message;
import Data.Models.User;
import DataAccess.ViewModels.MessageViewModel;
import DataAccess.ViewModels.UserViewModel;
import Services.ChannelService;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class TextChatFragment extends Fragment{
    MessageListener messageListener;

    Retrofit retrofit;
    ChannelService channelService;

    MessageAdapter messageAdapter;
    MessageViewModel messageViewModel;
    UserViewModel userViewModel;

    private long lastMessageId;

    String chatName;
    long chatID;

    FragmentContainerView fragmentContainerView;
    ConstraintLayout.LayoutParams layoutParams;

    public interface MessageListener {
        void loadMessagesFromServer(long channelId, Long lastMessageId);
        void sendMessage(String text, long channelId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            messageListener = (MessageListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(e.getMessage() + " must implements TextChatFragment.MessageListener interface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_chat, container, false);


//        Receive chat data from bundle
        Bundle receivedBundle = getArguments();
        if (receivedBundle != null){
            if (receivedBundle.containsKey("channelId")){
                chatID = receivedBundle.getLong("channelId");
            }
            if (receivedBundle.containsKey("channelName")){
                chatName = receivedBundle.getString("channelName");
            } else {
                chatName = "Chat name not found!";
            }
        }
        TextView chatName = view.findViewById(R.id.textChatName);
        chatName.setText(getArguments().getString("channelName"));

        retrofit = new Retrofit.Builder()
                .baseUrl(env.api)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        channelService = retrofit.create(ChannelService.class);

//        Displaying messages
        messageViewModel = new ViewModelProvider(requireActivity()).get(MessageViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        messageAdapter = new MessageAdapter(requireActivity());
        RecyclerView recyclerView = view.findViewById(R.id.messagesRecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findFirstVisibleItemPosition() == 0) {
                    long lastMessageId = 1;
                    messageListener.loadMessagesFromServer(chatID, lastMessageId);
                }
            }
        });
        messageListener.loadMessagesFromServer(chatID, null);
        messageViewModel.getAllMessagesFromChannel(chatID).observe(getViewLifecycleOwner(), messages -> {
            if (messages != null){
                messageAdapter.setMessagesList(messages);
            }
        });

//        Sending messagess
        EditText messageText = view.findViewById(R.id.inputText);
        Button sendMessageButton = view.findViewById(R.id.messageSendButton);
        sendMessageButton.setOnClickListener(v -> {
            messageListener.sendMessage(messageText.getText().toString(), chatID);
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
//            Clear messageText value and focus
            messageText.getText().clear();
            messageText.clearFocus();
//            Hide system keyboard
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().findViewById(R.id.communitySettingsButton).setVisibility(View.VISIBLE);
//        requireActivity().findViewById(R.id.bottom_navbar).setVisibility(View.GONE);

    }

    @Override
    public void onStop() {
        super.onStop();
//        requireActivity().findViewById(R.id.bottom_navbar).setVisibility(View.VISIBLE);
    }

}