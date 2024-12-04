package Fragments.Settings;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.szampchat.R;

public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.slide_left));
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Bundle received = getArguments();
        boolean isExtended = received.getBoolean("EXTENDED");
        long communityId = received.getLong("communityId");
        Log.d("SettingsFragment", "communityId = " + communityId);
        if (isExtended) Log.d("EXTENDED SETTINGS", "YES");

        LinearLayout profileSettings = view.findViewById(R.id.settingsUserProfile);
        LinearLayout rolesSettings = view.findViewById(R.id.settingsRoles);
        LinearLayout channelsSettings = view.findViewById(R.id.settingsChannels);

        rolesSettings.setVisibility(View.INVISIBLE);
        channelsSettings.setVisibility(View.INVISIBLE);

        if (isExtended){
            rolesSettings.setVisibility(View.VISIBLE);
            channelsSettings.setVisibility(View.VISIBLE);
            Bundle fragmentArgs = new Bundle();
            fragmentArgs.putLong("communityId", communityId);
            RolesFragment rolesFragment = new RolesFragment();
            ChannelsSettingsFragment channelsSettingsFragment = new ChannelsSettingsFragment();
            rolesFragment.setArguments(fragmentArgs);
            channelsSettingsFragment.setArguments(fragmentArgs);

//            Setup onClickListener to replace current fragment with RolesFragment
            rolesSettings.setOnClickListener(v -> {
//                requireActivity().getSupportFragmentManager().popBackStack("uniqueSettingsFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, rolesFragment)
                        .addToBackStack(null)
                        .commit();
            });
//            Setup onClickListener to replace current fragment with ChannelsFragment
            channelsSettings.setOnClickListener(v -> {
//                requireActivity().getSupportFragmentManager().popBackStack("uniqueSettingsFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, channelsSettingsFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
//        Setup onClickListener to replace current fragment with ProfileFragment
        profileSettings.setOnClickListener(v -> {
//            requireActivity().getSupportFragmentManager().popBackStack("uniqueSettingsFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }
}