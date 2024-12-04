package Fragments.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.szampchat.R;

import Config.env;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ProfileFragment extends Fragment {
    ImageView profilePicture;
    TextInputEditText profileUsername;
    TextInputEditText profileDescription;
    ExtendedFloatingActionButton editProfileButton;
    ExtendedFloatingActionButton cancelEditing;
    ExtendedFloatingActionButton acceptEditig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(env.api)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

//        TODO dodać wczytanie i edycje profilowego

        profilePicture = view.findViewById(R.id.profilePicture);
        profileUsername = view.findViewById(R.id.profileUsername);
        profileDescription = view.findViewById(R.id.profileAbout);

        setUserFromSharedPref(sharedPreferences);

        editProfileButton = view.findViewById(R.id.editProfileButton);
        cancelEditing = view.findViewById(R.id.cancelEditButton);
        acceptEditig = view.findViewById(R.id.acceptEditButton);

        TextInputLayout usernameLayout = view.findViewById(R.id.profileUsernameLayout);
        setTextChangeListener(profileUsername, usernameLayout);

        editProfileButton.setOnClickListener(v -> {
            toggleVisibility(true);
        });
        acceptEditig.setOnClickListener(v -> {
            String username = profileUsername.getText().toString();
            String description = profileDescription.getText().toString();

            if (username.matches("")) usernameLayout.setError("Podaj nazwę użytkownika!");
            else {
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("username", username);
//                editor.putString("description", description);
//                editor.apply();
                toggleVisibility(false);
            }
        });
        cancelEditing.setOnClickListener(v -> {
            setUserFromSharedPref(sharedPreferences);
            toggleVisibility(false);
        });



        return view;
    }
    private void toggleVisibility(boolean isEditing) {
        profileUsername.setFocusable(isEditing);
        profileUsername.setFocusableInTouchMode(isEditing);
        profileDescription.setFocusable(isEditing);
        profileDescription.setFocusableInTouchMode(isEditing);

        editProfileButton.setVisibility(isEditing ? View.INVISIBLE : View.VISIBLE);
        acceptEditig.setVisibility(isEditing ? View.VISIBLE : View.INVISIBLE);
        cancelEditing.setVisibility(isEditing ? View.VISIBLE : View.INVISIBLE);
    }
    private void setUserFromSharedPref(SharedPreferences sharedPreferences){
        sharedPreferences.getString("imageUrl", "Image Not Found");
        profileUsername.setText(sharedPreferences.getString("username", "User Not Found :("));
        profileDescription.setText(sharedPreferences.getString("description", "No description from this user :("));
    }
    private void setTextChangeListener(TextInputEditText textInputEditText, TextInputLayout textInputLayout){
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayout.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}