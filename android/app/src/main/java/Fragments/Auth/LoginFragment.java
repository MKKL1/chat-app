package Fragments.Auth;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.material.color.MaterialColors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.szampchat.R;

public class LoginFragment extends Fragment {

    LoginListener loginListener;

//    Functions required by this fragment to implement
    public interface LoginListener {
        void verifyLogin(String username, String password);
        void switchToRegister();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            loginListener = (LoginListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LoginListener interface");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

//        Setup login form view elements
        TextInputEditText login = view.findViewById(R.id.loginTextField);
        TextInputEditText password = view.findViewById(R.id.passwordTextField);

        TextInputLayout loginLayout = view.findViewById(R.id.loginTextFieldLayout);
        TextInputLayout passwordLayout = view.findViewById(R.id.passwordTextFieldLayout);

        setTextWatcher(login, loginLayout);
        setTextWatcher(password, passwordLayout);

        Button registerButton = (Button) view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener( v -> {
            loginListener.switchToRegister();
        });

        Button loginButton = (Button) view.findViewById(R.id.loginButton);


//        Login button clicked
        loginButton.setOnClickListener( v -> {
            boolean validate = true;
            String loginText = login.getText().toString();
            String passwordText = password.getText().toString();

//            Validate login form
            if (loginText.matches("")) {
                loginLayout.setError("Podaj login!");
                validate = false;
            };
            if (passwordText.matches("")) {
                passwordLayout.setError("Podaj hasło!");
                validate = false;
            }
            if (validate) loginListener.verifyLogin(login.getText().toString(), password.getText().toString());
        });
        return view;
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