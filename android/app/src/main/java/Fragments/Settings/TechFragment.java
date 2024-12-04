package Fragments.Settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.szampchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Adapters.UserAdapter;
import DataAccess.ViewModels.UserViewModel;

// TODO przy edycji roli trzeba wczytać i zaznacyć użytkowników którzy ją posiadają
public class TechFragment extends Fragment {
    long communityId;

    Long roleId;
    Long permissions;
    String roleName;

    RolesListener rolesListener;
    UserViewModel userViewModel;

    public interface RolesListener{
        void addRole(String name, long permission, List<Long> members);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            rolesListener = (RolesListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage() + " must implements RolesFragment.RolesListener");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tech, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        UserAdapter userAdapter = new UserAdapter(requireActivity(), true);

        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null){
                userAdapter.setUserList(users.stream().filter(x->x.communitiesList.contains(communityId)).collect(Collectors.toList()));
            }
        });

        RecyclerView userRecyclerView = view.findViewById(R.id.roleUsersRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userRecyclerView.setAdapter(userAdapter);

        TextView createRoleTitle = view.findViewById(R.id.createRoleTitle);
        TextInputEditText roleNameInput = view.findViewById(R.id.createRoleNameInput);
        TextInputLayout roleNameLayout = view.findViewById(R.id.createRoleNameLayout);

        SwitchMaterial setting1Switch = view.findViewById(R.id.roleSetting1Switch);
        SwitchMaterial setting2Switch = view.findViewById(R.id.techSetting2Switch);
        SwitchMaterial setting3Switch = view.findViewById(R.id.techSetting3Switch);
        SwitchMaterial setting4Switch = view.findViewById(R.id.techSetting4Switch);
        SwitchMaterial setting5Switch = view.findViewById(R.id.techSetting5Switch);
        SwitchMaterial setting6Switch = view.findViewById(R.id.techSetting6Switch);
        SwitchMaterial setting7Switch = view.findViewById(R.id.techSetting7Switch);
        SwitchMaterial setting8Switch = view.findViewById(R.id.techSetting8Switch);

        try {
            communityId = getArguments().getLong("communityID");
        } catch (NullPointerException e) {
            throw new NullPointerException("communityID from fragment's arguments is null");
        }
        Bundle received = getArguments();
        if (received.containsKey("roleId")){
            permissions = getArguments().getLong("permissions", 0);
            roleId = getArguments().getLong("roleId", 0);
            roleName = getArguments().getString("roleName", "UNKNOWN");
            roleNameInput.setText(roleName);
            createRoleTitle.setText("EDYCJA ROLI");
            setting1Switch.setChecked((permissions & (1L << 0)) != 0);
            setting2Switch.setChecked((permissions & (1L << 1)) != 0);
            setting3Switch.setChecked((permissions & (1L << 2)) != 0);
            setting4Switch.setChecked((permissions & (1L << 3)) != 0);
            setting5Switch.setChecked((permissions & (1L << 4)) != 0);
            setting6Switch.setChecked((permissions & (1L << 5)) != 0);
            setting7Switch.setChecked((permissions & (1L << 6)) != 0);
            setting8Switch.setChecked((permissions & (1L << 7)) != 0);
        }


        TextView techSetting1Input = view.findViewById(R.id.techSetting1Input);
        techSetting1Input.setText("Administrator:");

        TextView techSetting2Input = view.findViewById(R.id.techSetting2Input);
        techSetting2Input.setText("Tworzenie/Modyfikacja ról:");

        TextView techSetting3Input = view.findViewById(R.id.techSetting3Input);
        techSetting3Input.setText("Tworzenie zaproszeń:");

        TextView techSetting4Input = view.findViewById(R.id.techSetting4Input);
        techSetting4Input.setText("Tworzenie kanałów:");

        TextView techSetting5Input = view.findViewById(R.id.techSetting5Input);
        techSetting5Input.setText("Modyfikacja kanałów:");

        TextView techSetting6Input = view.findViewById(R.id.techSetting6Input);
        techSetting6Input.setText("Wysyłanie wiadomości w kanałach tekstowych:");

        TextView techSetting7Input = view.findViewById(R.id.techSetting7Input);
        techSetting7Input.setText("Usuwanie wiadomości w kanałach tekstowych:");

        TextView techSetting8Input = view.findViewById(R.id.techSetting8Input);
        techSetting8Input.setText("Dodawanie reakcji do wiadomości:");

        Button saveButton = view.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            roleName = roleNameInput.getText().toString();
            if (roleName.matches("")){
                roleNameLayout.setError("Podaj nazwe roli!");
            } else {

                long permissionOverwrites = 224;
                if (setting1Switch.isChecked()) permissionOverwrites |= 1L << 0;
                if (setting2Switch.isChecked()) permissionOverwrites |= 1L << 1;
                if (setting3Switch.isChecked()) permissionOverwrites |= 1L << 2;
                if (setting4Switch.isChecked()) permissionOverwrites |= 1L << 3;
                if (setting5Switch.isChecked()) permissionOverwrites |= 1L << 4;
                if (setting6Switch.isChecked()) permissionOverwrites |= 1L << 5;
                if (setting7Switch.isChecked()) permissionOverwrites |= 1L << 6;
                if (setting8Switch.isChecked()) permissionOverwrites |= 1L << 7;
                rolesListener.addRole(roleName, permissionOverwrites, userAdapter.getSelectedUsers());

                Bundle fragmentArgs = new Bundle();
                fragmentArgs.putLong("communityId", communityId);
                RolesFragment rolesFragment = new RolesFragment();
                rolesFragment.setArguments(fragmentArgs);

                requireActivity().getSupportFragmentManager().popBackStack("uniqueSettingsFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, rolesFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}