package Services;

import Data.Models.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserService {

    @GET("/api/users/me")
    Call<User> getCurrentUser(
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @GET("/api/users")
    Call<User> getUser(
            @Header("Authorization") String token,
            @Field("userId") long userId);

    @POST("/api/users/description")
    Call<Void> updateUserDescription(
            @Field("descriptionDTO") String description
    );

    @FormUrlEncoded
    @POST("/api/users")
    Call<User> registerUser(
            @Header("Authorization") String token,
            @Field("username") String username
    );
}
