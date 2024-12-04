package Fragments.Community;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.szampchat.R;

import java.util.Arrays;

import Config.env;
import Data.DTO.LiveKitTokenResponse;
import Data.Models.Token;
import Services.ChannelService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class VoiceChatFragment extends Fragment {

    Retrofit retrofit;
    ChannelService channelService;
    String liveKitToken;

    Token token = new Token();

    long channelID;
    String channelName;
    boolean connected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice_chat, container, false);

        Bundle receivedBundle = getArguments();
        if (receivedBundle != null){
            if (receivedBundle.containsKey("channelId")){
                channelID = receivedBundle.getLong("channelId");
            }
            if (receivedBundle.containsKey("channelName")){
                channelName = receivedBundle.getString("channelName");
            } else {
                channelName = "Channel name not found!";
            }
        }

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app_prefs", MODE_PRIVATE);

        token.setAccessToken(sharedPreferences.getString("token", "TOKEN NOT FOUND"));

        TextView channelNameText = view.findViewById(R.id.channelName);
        Button connectButton = view.findViewById(R.id.connectButton);
        channelNameText.setText(channelName);

        retrofit = new Retrofit.Builder()
                .baseUrl(env.api)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        channelService = retrofit.create(ChannelService.class);

        Call<LiveKitTokenResponse> callGetLiveKitToken = channelService.joinVoiceChannel(
                "Bearer " + token.getAccessToken(),
                channelID
        );

        callGetLiveKitToken.enqueue(new Callback<LiveKitTokenResponse>() {
            @Override
            public void onResponse(Call<LiveKitTokenResponse> call, Response<LiveKitTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    liveKitToken = response.body().getToken();
                }
                else {
                    Log.d("VoiceChatFragment", "Błąd pobierania tokenu z serwera " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LiveKitTokenResponse> call, Throwable t) {
                Log.d("VoiceChatFragment", "Błąd wykonywania usługi " + Arrays.toString(t.getStackTrace()));
            }
        });

        connectButton.setOnClickListener(v -> {
            connected = !connected;

            if (connected){
                if (!liveKitToken.matches("")){
                    connectToLiveKit(liveKitToken);
                    connectButton.setText(R.string.disconnect);
                    connectButton.setTextColor(getResources().getColor(R.color.red));
                } else {
                    Toast.makeText(getContext(), "Zły token od serwera!", Toast.LENGTH_SHORT).show();
                }
            } else {
//                TODO rozłączyć połączenie z livekit
                connectButton.setText(R.string.connect);
                connectButton.setTextColor(getResources().getColor(R.color.light_green));
            }
        });



        return view;
    }

    private void connectToLiveKit(String token){
        Toast.makeText(getContext(), liveKitToken, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().findViewById(R.id.communitySettingsButton).setVisibility(View.VISIBLE);
    }
}