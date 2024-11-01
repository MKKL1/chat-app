package com.szampchat.server.voice;

import reactor.core.publisher.Mono;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Small tool to help convert livekit responses to reactive stream with proper exception handling
public class RetrofitToReactor {

    public static <T> Mono<T> toMono(Call<T> call) {
        return Mono.create(sink -> {
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        sink.success(response.body());
                    } else {
                        sink.error(new RuntimeException("Request failed: " + response.message()));
                    }
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    sink.error(t);
                }
            });
        });
    }
}
