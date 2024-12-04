package Fragments.Community;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.szampchat.R;

import Data.DTO.ChannelType;
import Data.Models.Channel;
import DataAccess.ViewModels.ChannelViewModel;
import DataAccess.ViewModels.RoleViewModel;
import DataAccess.ViewModels.UserViewModel;

public class CommunityWelcomeFragment extends Fragment {

    private UserViewModel userViewModel;
    private RoleViewModel roleViewModel;
    private ChannelViewModel channelViewModel;
    long communityId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
//        setExitTransition(inflater.inflateTransition(R.transition.slide_up));
//        setEnterTransition(inflater.inflateTransition(R.transition.slide_down));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_welcome, container, false);

        try {
            communityId = getArguments().getLong("communityID");
        } catch (NullPointerException e) {
            throw new NullPointerException("communityID from fragment's arguments is null");
        }

        TextView voiceChannelNumber = view.findViewById(R.id.voiceChannelNumber);
        TextView textChannelNumber = view.findViewById(R.id.textChannelNumber);
        TextView usersNumber = view.findViewById(R.id.usersNumber);
        TextView rolesNumber = view.findViewById(R.id.rolesNumber);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("CommunityData", Context.MODE_PRIVATE);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        roleViewModel = new ViewModelProvider(requireActivity()).get(RoleViewModel.class);
        channelViewModel = new ViewModelProvider(requireActivity()).get(ChannelViewModel.class);

        // Obserwowanie LiveData w ViewModelach
        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            Long usersCount = users.stream().filter(x->x.communitiesList.contains(communityId)).count();
            usersNumber.setText(String.valueOf(usersCount));
        });

        roleViewModel.getRolesForCommunity(communityId).observe(getViewLifecycleOwner(), roles -> {
            rolesNumber.setText(String.valueOf(roles.size()));
        });

        channelViewModel.getChannelsFromCommunity(communityId).observe(getViewLifecycleOwner(), channels -> {
            long voiceChannels = channels.stream().filter(x->x.getType().equals(ChannelType.VOICE_CHANNEL)).count();
            long textChannels = channels.size() - voiceChannels;
            voiceChannelNumber.setText(String.valueOf(voiceChannels));
            textChannelNumber.setText(String.valueOf(textChannels));
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().findViewById(R.id.communitySettingsButton).setVisibility(View.VISIBLE);
    }
}