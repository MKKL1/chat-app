package Fragments.Settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.szampchat.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Adapters.RoleAdapter;
import Data.Models.Role;
import DataAccess.ViewModels.RoleViewModel;

//TODO pokazuje za kazdym razem wszystkie Role z Room'a, nie wiem kurwa czemu jest napiasne 1:1 jak Channel a tamto dziala

public class RolesFragment extends Fragment {
    long communityId;
    RoleAdapter roleAdapter;
    RoleViewModel roleViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roles, container, false);

        try {
            communityId = getArguments().getLong("communityId");
            Log.d("CommunityId", String.valueOf(communityId));
        } catch (NullPointerException e) {
            throw new NullPointerException("communityID from fragment's arguments is null");
        }


        roleViewModel = new ViewModelProvider(requireActivity()).get(RoleViewModel.class);
        roleAdapter = new RoleAdapter(requireActivity());
        roleViewModel.getRolesForCommunity(communityId).observe(getViewLifecycleOwner(), roles -> {
            if (roles != null){
                roleAdapter.setRoleList(roles);
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.roleRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(roleAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(roleAdapter.getItemTouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        Button createRoleButton = view.findViewById(R.id.createRoleButton);
        createRoleButton.setOnClickListener(v -> {
            TechFragment techFragment = new TechFragment();
            Bundle args = new Bundle();
            args.putLong("communityID", communityId);
            techFragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, techFragment)
                    .addToBackStack("uniqueSettingsFrag")
                    .commit();
        });

        return view;
    }
}