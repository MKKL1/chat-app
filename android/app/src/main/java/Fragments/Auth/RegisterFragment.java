package Fragments.Auth;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.szampchat.R;
public class RegisterFragment extends Fragment {

    RegisterListener registerListener;
    boolean validate;

    public interface RegisterListener {
        void registerUser(String username, String email, String password, String passwordCheck);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            registerListener = (RegisterListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RegisterListener interface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.slide_left));
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        TextInputEditText username = view.findViewById(R.id.usernameTextField);
        TextInputEditText email = view.findViewById(R.id.emailTextField);
        TextInputEditText password = view.findViewById(R.id.passwordTextField);
        TextInputEditText passwordCheck = view.findViewById(R.id.passwordCheckTextField);

        TextInputLayout usernameLayout = view.findViewById(R.id.usernameTextFieldLayout);
        TextInputLayout emailLayout = view.findViewById(R.id.emailTextFieldLayout);
        TextInputLayout passwordLayout = view.findViewById(R.id.passwordTextFieldLayout);
        TextInputLayout passwordCheckLayout = view.findViewById(R.id.passwordCheckTextFieldLayout);

        setTextWatcher(username, usernameLayout);
        setTextWatcher(email, emailLayout);
        setTextWatcher(password, passwordLayout);
        setTextWatcher(passwordCheck, passwordCheckLayout);

        Button registerButton = view.findViewById(R.id.createAccountButton);
        registerButton.setOnClickListener(v -> {
            String
                    usernameText = username.getText().toString(),
                    emailText = email.getText().toString(),
                    passwordText = password.getText().toString(),
                    passwordCheckText = passwordCheck.getText().toString();
            validate = true;
            if (usernameText.matches("")) {
                usernameLayout.setError("Podaj nazwe użytkownika!");
                deny();
            };
            if (emailText.matches("")) {
                emailLayout.setError("Podaj adres email!");
                deny();
            }
            if (passwordText.matches("")) {
                passwordLayout.setError("Podaj hasło!");
                deny();
            }
            else if (passwordCheckText.matches("")) {
                passwordCheckLayout.setError("Powtórz hasło!");
                deny();
            }
            else if (!passwordText.matches(passwordCheckText)) {
                passwordCheckLayout.setError("Hasła się nie zgadzają!");
                deny();
            }
            if (validate){
//                    Pass params to AuthActivity function
                registerListener.registerUser(usernameText,emailText,passwordText,passwordCheckText);
            }
        });
        return view;
    }
    private void deny(){
        validate = false;
    }
    private void setTextWatcher(TextInputEditText textInputEditText, TextInputLayout textInputLayout){
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