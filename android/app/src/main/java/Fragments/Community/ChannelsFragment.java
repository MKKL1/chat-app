package Fragments.Community;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szampchat.R;

import java.util.stream.Collectors;

import Adapters.ChannelAdapter;
import DataAccess.ViewModels.ChannelViewModel;

public class ChannelsFragment extends Fragment {

    ChannelViewModel channelViewModel;
    ChannelAdapter channelAdapter;
    long communityID;
    String channelType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channels, container, false);

        try {
            communityID = getArguments().getLong("communityID");
        } catch (NullPointerException e) {
            throw new NullPointerException("communityID from fragment's arguments is null");
        }
        channelType = getArguments().getString("type");
        channelAdapter = new ChannelAdapter(requireActivity(), false);
        channelViewModel = new ViewModelProvider(requireActivity()).get(ChannelViewModel.class);


        if (channelType.matches("")) Log.d("ChannelsFragment", "Brak typu kanałów przekazanego w Arguments");
        channelViewModel.getChannelsFromCommunity(communityID)
                .observe(getViewLifecycleOwner(), channels -> {
                    if (channels != null) {
                        channelAdapter.setChannelsList(channels.stream().filter(x->x.getType().name().equals(channelType)).collect(Collectors.toList()));
                    }
                });

//        Setup RecyclerView to show channels from specific community
        RecyclerView recyclerView = view.findViewById(R.id.channelsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(channelAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().findViewById(R.id.communitySettingsButton).setVisibility(View.VISIBLE);
    }
}