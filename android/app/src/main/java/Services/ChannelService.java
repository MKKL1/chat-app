package Services;

import java.util.List;

import Data.DTO.ChannelDTO;
import Data.DTO.LiveKitTokenResponse;
import Data.Models.Message;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChannelService {

    @POST("/api/communities/{communityId}/channels")
    Call<ChannelDTO> createChannel(
            @Header("Authorization") String token,
            @Path("communityId") long communityId,
            @Body RequestBody channel
            );

    @DELETE("/api/channels/{channelId}")
    Call<Void> deleteChannel(
            @Header("Authorization") String token,
            @Path("channelId") long channelId
    );

    @GET("/api/channels/{channelId}/messages")
    Call<List<Message>> getFirstMessagesForChannel(
            @Header("Authorization") String token,
            @Path("channelId") long channelId,
            @Query("limit") int limit
    );
    @GET("/api/channels/{channelId}/messages")
    Call<List<Message>> getMessagesForChannel(
            @Header("Authorization") String token,
            @Path("channelId") long channelId,
            @Query("limit") int limit,
            @Query("before") Long lastMessageId
    );
    @PUT("/api/channels/{channelId}")
    Call<ChannelDTO> editChannel(
            @Header("Authorization") String token,
            @Path("channelId") long channelId
    );
    @Multipart
    @POST("/api/channels/{channelId}/messages")
    Call<Message> createMessage(
            @Header("Authorization") String token,
            @Path("channelId") long channelId,
            @Part("message") RequestBody message
//            @Part("file") MultipartBody.Part file
    );

    @POST("/api/channels/{channelId}/messages/{messageId}/reactions")
    Call<Void> createReaction(
            @Header("Authorization") String token,
            @Path("channelId") long channelId,
            @Path("messageId") long messageId,
            @Body RequestBody emoji
    );

    @Multipart
    @DELETE("/api/channels/{channelId}/messages/{messageId}/reactions")
    Call<Void> deleteReaction(
            @Header("Authorization") String token,
            @Path("channelId") long channelId,
            @Path("messageId") long messageId,
            @Part("emoji") RequestBody emoji
    );

    @GET("/api/channels/{channelId}/voice/join")
    Call<LiveKitTokenResponse> joinVoiceChannel(
            @Header("Authorization") String token,
            @Path("channelId") long channelId
    );

    @DELETE("/api/channels/{channelId}/messages/{messageId}")
    Call<Void> deleteMessage(
            @Header("Authorization") String token,
            @Path("channelId") long channelId,
            @Path("messageId") long messageId
    );

    @PATCH("/api/channels/{channelId}/messages/{messageId}")
    Call<Message> editMessage(
            @Header("Authorization") String token,
            @Path("channelId") long channelId,
            @Path("messageId") long messageId
    );
}
