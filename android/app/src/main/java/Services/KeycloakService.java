package Services;

import Data.Models.Token;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface KeycloakService {

    String realm = "szampchat";
//    TODO ogarnac rejestracje
    @POST("/realms/" + realm + "/clients-registrations/openid-connect")
    Call<Token> registerUser(

    );

    @POST("/realms/" + realm + "/protocol/openid-connect/token")
    @FormUrlEncoded
    Call<Token> getAccessToken(
            @Field("client_id") String clientId,
            @Field("grant_type") String grantType,
            @Field("username") String username,
            @Field("password") String password
    );
    @POST("token")
    @FormUrlEncoded
    Call<Token> refreshAccessToken(
            @Field("client_id") String client_id,
            @Field("grant_type") String grant_type
    );
    @POST("logout")
    @FormUrlEncoded
    void logout();
}