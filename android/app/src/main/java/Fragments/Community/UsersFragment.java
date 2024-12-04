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

import Adapters.UserAdapter;
import DataAccess.ViewModels.UserViewModel;

public class UsersFragment extends Fragment {
    UserAdapter userAdapter;
    UserViewModel userViewModel;
    long communityId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TransitionInflater inflater = TransitionInflater.from(requireContext());
//        setExitTransition(inflater.inflateTransition(R.transition.slide_up));
//        setEnterTransition(inflater.inflateTransition(R.transition.slide_down));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        try {
            communityId = getArguments().getLong("communityID");
        } catch (NullPointerException e) {
            throw new NullPointerException("communityID from fragment's arguments is null");
        }

        userAdapter = new UserAdapter(requireActivity(), false);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null){
                userAdapter.setUserList(users.stream().filter(x->x.communitiesList.contains(communityId)).collect(Collectors.toList()));
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.usersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(userAdapter);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        requireActivity().findViewById(R.id.communitySettingsButton).setVisibility(View.VISIBLE);
    }
}