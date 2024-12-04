package Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.szampchat.R;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import Data.Models.User;
import Services.KeycloakService;
import Data.Models.Token;
import Services.UserService;
import Config.env;
import Fragments.Auth.LoginFragment;
import Fragments.Auth.RegisterFragment;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class AuthActivity extends AppCompatActivity
        implements LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    KeycloakService keycloakService;
    Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .connectTimeout(500, TimeUnit.MILLISECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(env.keycloakUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        keycloakService = retrofit.create(KeycloakService.class);

//        skip login
        verifyLogin("test", "test");
    }

    /**
     * Function calling api for user authentication and receive token which is sent to MainActivity with Intent
     * @param username username from login form
     * @param password password from login form
     */

    @Override
    public void verifyLogin(String username, String password) {
        token = new Token();

        Call<Token> tokenCall = keycloakService.getAccessToken(
                env.keycloakClientId,
                "password",
                username,
                password
        );
        tokenCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful() && response.body() != null) {
                    token.setAccessToken(response.body().getAccessToken());
                    token.setExpiresIn(response.body().getExpiresIn());
                    token.setRefreshExpiresIn(response.body().getRefreshExpiresIn());
                    token.setRefreshToken(response.body().getRefreshToken());
                    token.setTokenType(response.body().getTokenType());

                    Log.d("AuthActivity - login", "Token: " + token.getAccessToken());
                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                    saveToken(getApplicationContext(), token.getAccessToken());
                    startActivity(intent);
                } else {
                    Log.e("AuthActivity - login", "Błąd logowania: " + response.code() + " " + response.message());
                    if(response.code() == 401){
                        TextInputLayout passwordLayout = findViewById(R.id.passwordTextFieldLayout);
                        TextInputLayout loginLayout = findViewById(R.id.loginTextFieldLayout);
                        TextInputEditText password = findViewById(R.id.passwordTextField);
                        password.setText("");
                        passwordLayout.setError("Niepoprawne dane");
                        loginLayout.setError("Niepoprawne dane");
                    }
                }
            }
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("AuthActivity - login", "Błąd podczas wywoływania usługi: " + t.getMessage());
                Toast.makeText(AuthActivity.this, "Brak połączenia z serwerem!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Replace fragment container in AuthActivity with Register From Framgent
     */
    @Override
    public void switchToRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.authFragmentContainer, new RegisterFragment())
                .addToBackStack(null)
                .setReorderingAllowed(true)
                .commit();
    }

    @Override
    public void registerUser(String username, String email, String password, String passwordCheck) {
        token = new Token();

        Call<Token> tokenCall = keycloakService.getAccessToken(
                env.keycloakClientId,
                "password",
                username,
                password
        );
        tokenCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AuthActivity - register", "Odpowiedź:" + response.code() + " - " + response.message());
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(env.api)
                            .addConverterFactory(JacksonConverterFactory.create())
                            .build();

                    token.setAccessToken(response.body().getAccessToken());
                    token.setExpiresIn(response.body().getExpiresIn());
                    token.setRefreshExpiresIn(response.body().getRefreshExpiresIn());
                    token.setRefreshToken(response.body().getRefreshToken());
                    token.setTokenType(response.body().getTokenType());
                    saveToken(getApplicationContext(), token.getAccessToken());
                    Log.d("AuthActivity - register", "Token: " + token.getAccessToken());

                    UserService userService = retrofit.create(UserService.class);
                    Call<User> userInfoCall = userService.registerUser("Bearer "+token.getAccessToken(), username);
                    userInfoCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                            if (response.isSuccessful() && response.body() != null){
                                if (response.body().getUsername() != null)
                                {
                                    Log.d("UserInfo - id", ""+response.body().getUserId());
                                    Log.d("UserInfo - username", response.body().getUsername());

                                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                            else {
                                Log.d("AuthActivity - register", "Błąd rejestracji Spring: " + response.code() + response.message());
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                            Log.d("AuthActivity - register", "Nieudane połączenie do serwera" + Arrays.toString(t.getStackTrace()));
                        }
                    });
                } else {
                    Log.e("AuthActivity - register", "Błąd rejestracji Keycloak: " + response.code() + " " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("AuthActivity - register", "Błąd podczas wywoływania usługi Keycloak: " + t.getMessage());
                Toast.makeText(AuthActivity.this, "Brak połączenia z serwerem Keycloak!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Saving token to SharedPreferences
     * @param context - interface where to save token
     * @param token - jwt token
     */
//    TODO przeniesc token do Keystore?
    public void saveToken(Context context, String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }
}