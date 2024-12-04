package Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.szampchat.R;

import java.util.Arrays;
import java.util.List;

import Adapters.CommunityAdapter;
import Data.Models.Token;
import Data.Models.Community;
import Data.Models.User;
import Services.CommunityService;
import Services.UserService;
import Config.env;
import DataAccess.ViewModels.CommunityViewModel;
import Fragments.MainFragment;
import Fragments.Settings.SettingsFragment;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends AppCompatActivity implements
        CommunityAdapter.OnItemClickListener,
        MainFragment.MainFragmentListener
{
    Retrofit retrofit;
    CommunityViewModel communitiesViewModel;
    Button settingsButton;
    Token token = new Token();
    UserService userService;
    CommunityService communityService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //        Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Społeczności");


        communitiesViewModel = new ViewModelProvider(this).get(CommunityViewModel.class);

//        Load token from SharedPreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app_prefs", MODE_PRIVATE);
        token.setAccessToken(sharedPreferences.getString("token", "TOKEN NOT FOUND"));

        retrofit = new Retrofit.Builder()
                .baseUrl(env.api)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        userService = retrofit.create(UserService.class);
        communityService = retrofit.create(CommunityService.class);

//        Get user
        fetchUserInfo(sharedPreferences);

//        Button displaying Settings section (fragment)
        settingsButton = findViewById(R.id.mainSettingsButton);
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle settingsArgs = new Bundle();
        settingsArgs.putBoolean("EXTENDED", false);
        settingsFragment.setArguments(settingsArgs);
        settingsButton.setOnClickListener(v -> {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, settingsFragment)
                    .addToBackStack(null)
                    .commit();
//            Hide settings button after displaying SettingsFragment
            settingsButton.setVisibility(View.INVISIBLE);
        });

//        Download list of user's communities from API
        fetchCommunities();

//        Initial setup fragment with communities recyclerview, passed adapter to constructor
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new MainFragment())
                .commit();
    }

    /**
     * Fetch user info from Server API and save it in SharedPreferences
     * @param sharedPreferences data store where to save
     */
    private void fetchUserInfo(SharedPreferences sharedPreferences){
        Call<User> userInfoCall = userService.getCurrentUser(
                "Bearer "+token.getAccessToken()
        );
        userInfoCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null){
                    if (response.body().getUsername() != null)
                    {
                        Log.d("MainActivity UserInfo - id", ""+response.body().getUserId());
                        Log.d("MainActivity UserInfo - username", response.body().getUsername());

                        long id = response.body().getUserId();
                        String username = response.body().getUsername();
//                    TODO przerobic na proto DataStore np.
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("userId", id);
                        editor.putString("username", username);
                        getSupportActionBar().setTitle("YO! "+username);
                        if (response.body().getDescription() != null) {
                            String description = response.body().getDescription();
                            Log.d("UserInfo - description:", description);
                            editor.putString("description", description);
                        }
                        if (response.body().getImageUrl() != null){
                            String imageUrl = response.body().getImageUrl();
                            Log.d("UserInfo - imageUrl", imageUrl);
                            editor.putString("imageUrl", imageUrl);
                        }
                        editor.apply();
                    }
                }
                else {
                    Log.d("MainActivity", "Błąd pobierania danych o użytkowniku" + response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.d("MainActivity", "Nieudane połączenie do serwera" + Arrays.toString(t.getStackTrace()));
            }
        });
    }

    /**
     * Fetch all communities for logged user from Server API
     */
    private void fetchCommunities(){
        Call<List<Community>> communitiesCall = communityService.getCommunities(
                "Bearer "+token.getAccessToken()
        );
        communitiesCall.enqueue(new Callback<List<Community>>() {
            @Override
            public void onResponse(Call<List<Community>> call, Response<List<Community>> response) {
                if (response.isSuccessful() && response.body()!=null){
                    Log.d("MainActivity - communitiesCall", "Ilość społeczności: " + response.body().size());
                    List<Community> communityList = response.body();
                    for (Community community : communityList) {
                        Log.d("MainActivity - communitiesCall", "Nazwa społeczności: " + community.getCommunityName());
//                        If communities already exists in Room database onConflictStrategy = REPLACE
                        communitiesViewModel.addCommunity(community);
                    }
                } else {
                    Log.d("MainActivity - communitiesCall", "Błąd pobierania danych z serwera" + response.code() + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Community>> call, Throwable t) {
                Log.d("MainActivity - communitiesCall", Arrays.toString(t.getStackTrace()));
            }
        });
    }
    /**
     * Showing dialog to enter community join code
     */
    @Override
    public void callAddCommunityDialog() {
        final Dialog addCommunityDialog = new Dialog(this);
        addCommunityDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        addCommunityDialog.setContentView(R.layout.single_input_dialog);
        TextInputLayout joinCodeLayout = addCommunityDialog.findViewById(R.id.dialogInputLayout);
        TextInputEditText joinCode = addCommunityDialog.findViewById(R.id.dialogInput);
        Button joinButton = addCommunityDialog.findViewById(R.id.dialogButton);
//        TODO zmienic na pobranie spolecznosci z serwera i dołączenie do niej
        joinButton.setOnClickListener(v -> {
            String joinCodeText = joinCode.getText().toString();
            if (joinCodeText.matches("")){
                joinCodeLayout.setError("Pole wymagane!");
            } else {
                addCommunityDialog.dismiss();
            }
//            mCommunitiesViewModel.addCommunity(new CommunityModel(joinCode.getText().toString()));
        });
        joinCodeLayout.setHint("Podaj kod dołączenia");
        joinButton.setText("DOŁĄCZ");
        addCommunityDialog.show();
    }

    /**
     * Showing dialog to create new community
     */
    @Override
    public void callCreateCommunityDialog() {
        final Dialog createCommunityDialog = new Dialog(this);
        createCommunityDialog.setContentView(R.layout.single_input_dialog);
        createCommunityDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        TextInputLayout communityNameLayout = createCommunityDialog.findViewById(R.id.dialogInputLayout);
        TextInputEditText communityName = createCommunityDialog.findViewById(R.id.dialogInput);
        Button createButton = createCommunityDialog.findViewById(R.id.dialogButton);
//        TODO dodac wgranie zdjecia na ikonke spolecznosci
        createButton.setOnClickListener(v -> {
            String name = communityName.getText().toString();
            if (name.matches("")){
                communityNameLayout.setError("Pole wymagane!");
            } else {
                RequestBody communityJson = RequestBody.create(
                        MediaType.parse("application/json"),
                        "{\"name\": \"" + name + "\"}"
                );
                Call<Community> createCommunityCall = communityService.createCommunity(
                        "Bearer "+token.getAccessToken(),
                        communityJson
                );
                createCommunityCall.enqueue(new Callback<Community>() {
                    @Override
                    public void onResponse(Call<Community> call, Response<Community> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            communitiesViewModel.addCommunity(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Community> call, Throwable t) {
                        Log.d("MainActivity - createCommunityDialog ERROR", Arrays.toString(t.getStackTrace()));
                    }
                });
                createCommunityDialog.dismiss();
            }
        });
        communityNameLayout.setHint("Podaj nazwę społeczności");
        createButton.setText("STWÓRZ");
        createCommunityDialog.show();
    }
    /**
     * Move user to Activity with specific community he clicked
     */
    @Override
    public void onItemClickListener(Community community) {
        Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
        intent.putExtra("communityID", community.getCommunityID());
        intent.putExtra("communityName", community.getCommunityName());
        startActivity(intent);
    }

}