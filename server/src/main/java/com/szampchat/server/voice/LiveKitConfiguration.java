package com.szampchat.server.voice;

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
//import retrofit2.converter.protobuf.ProtoConverterFactory;
import io.livekit.server.RoomService;
import io.livekit.server.RoomServiceClient;
import io.livekit.server.okhttp.OkHttpFactory;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Configuration
public class LiveKitConfiguration {

    @Bean
    public RoomServiceClient roomServiceClient() {
        return RoomServiceClient.createClient(
                "http://localhost:7880",
                "devkey",
                "secret");
    }
}
