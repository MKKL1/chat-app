package Fragments.Settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.szampchat.R;

import java.util.ArrayList;
import java.util.List;

import Adapters.ChannelAdapter;
import Data.DTO.ChannelType;
import Data.Models.Channel;
import Data.Models.User;
import DataAccess.ViewModels.ChannelViewModel;

public class ChannelsSettingsFragment extends Fragment {

    ChannelsListener channelsListener;
    private ChannelType channelType;

    public interface ChannelsListener {
        void addChannel(String name, String type);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            channelsListener = (ChannelsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage() + " must implements ChannelsFragment.ChannelListener");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_channels_settings, container, false);

        Bundle received = getArguments();

        Button addVoiceChannel = view.findViewById(R.id.addVoiceChannelButton);
        Button addTextChannel = view.findViewById(R.id.addTextChannelButton);

        final Dialog createChannelDialog = new Dialog(requireContext());
        createChannelDialog.setContentView(R.layout.single_input_dialog);
        createChannelDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        TextInputLayout dialogLayout = createChannelDialog.findViewById(R.id.dialogInputLayout);
        TextInputEditText dialogEditText = createChannelDialog.findViewById(R.id.dialogInput);
        Button dialogButton = createChannelDialog.findViewById(R.id.dialogButton);
        dialogButton.setText(R.string.add);

        ChannelAdapter voiceChannelAdapter = new ChannelAdapter(requireActivity(), true);
        ChannelAdapter textChannelAdapter = new ChannelAdapter(requireActivity(), true);
        ChannelViewModel channelViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);

//        fetch channels for specific community
        if (received != null && received.containsKey("communityId")){
            channelViewModel.getChannelsFromCommunity(received.getLong("communityId"))
                    .observe(getViewLifecycleOwner(), channels -> {
                        if (channels != null) {
                            List<Channel> voiceChannels = new ArrayList<>();
                            List<Channel> textChannels = new ArrayList<>();
                            for (Channel channel : channels) {
                                if (channel.getType().equals(ChannelType.VOICE_CHANNEL)) voiceChannels.add(channel);
                                else if (channel.getType().equals(ChannelType.TEXT_CHANNEL)) textChannels.add(channel);
                                else Log.d("ChannelsSettingsFragment", "Nieznany typ kanału!\n" + channel.getType());
                            }
                            voiceChannelAdapter.setChannelsList(voiceChannels);
                            textChannelAdapter.setChannelsList(textChannels);
                        }
                    });
        }

        RecyclerView voiceChannelsRecycler = view.findViewById(R.id.channelsRecyclerView);
        voiceChannelsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        voiceChannelsRecycler.setAdapter(voiceChannelAdapter);

        RecyclerView textChannelsRecycler = view.findViewById(R.id.chatsRecyclerView);
        textChannelsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        textChannelsRecycler.setAdapter(textChannelAdapter);

        addVoiceChannel.setOnClickListener(v -> {
            Log.d("ChannelsFragment - addChannel", ChannelType.VOICE_CHANNEL.name());
//            channelListener.addChannel();
            dialogLayout.setHint("Nazwa kanału głosowego");
            dialogEditText.setText("");
            channelType = ChannelType.VOICE_CHANNEL;
            createChannelDialog.show();
        });
        addTextChannel.setOnClickListener(v -> {
            Log.d("ChannelsFragment - addChannel", ChannelType.TEXT_CHANNEL.name());
            dialogLayout.setHint("Nazwa kanału tekstowego");
            dialogEditText.setText("");
            channelType = ChannelType.TEXT_CHANNEL;
            createChannelDialog.show();
        });
        User user = new User();



        dialogButton.setOnClickListener(v -> {
            String channelName = dialogEditText.getText().toString();
            if (channelName.matches("")) dialogLayout.setError("Podaj nazwę kanału!");
            else {
                if (canPerformAction("CHANNEL_CREATE", user.getRoles())) {
                    channelsListener.addChannel(channelName, channelType.name());
                    createChannelDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Brak uprawnień!", Toast.LENGTH_SHORT);
                }
            }
        });

        return view;
    }

    private boolean canPerformAction(String action, List<Long> permissions){
        return true;
    }
}