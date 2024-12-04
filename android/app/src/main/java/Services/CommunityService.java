package Services;

import java.util.List;

import Data.DTO.FullCommunityDTO;
import Data.DTO.RoleResponseDTO;
import Data.Models.Community;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface CommunityService {

    @GET("/api/communities")
    Call<List<Community>> getCommunities(
            @Header("Authorization") String token
    );

    @GET("/api/communities/{communityId}/info")
    Call<FullCommunityDTO> getFullCommunityInfo(
            @Header("Authorization") String token,
            @Path("communityId") long id
    );

    @Multipart
    @POST("/api/communities")
    Call<Community> createCommunity(
            @Header("Authorization") String token,
            @Part("community") RequestBody community // JSON
            );

    @POST("/api/communities/{communityId}/join")
    Call<Void> joinCommunity(
            @Header("Authorization") String token,
            @Body RequestBody requestBody,
            @Path("communityId") long communityId
    );

    @Multipart
    @PATCH("/api/communities/{communityId}")
    Call<Community> editCommunity(
            @Header("Authorization") String token,
            @Path("communityId") long communityId,
//            @Part("file") MultipartBody.Part file,
            @Part("community") RequestBody community
    );

    @DELETE("/api/communities/{communityId}")
    Call<Void> deleteCommunity(
            @Header("Authorization") String token,
            @Path("communityId") long communityId
    );

    @POST("/api/communities/{communityId}/roles")
    Call<RoleResponseDTO> createRole(
            @Header("Authorization") String token,
            @Path("communityId") long communityId,
            @Body RequestBody requestBody
            );

    @DELETE("/api/roles/{roleId}")
    Call<Void> deleteRole(
            @Header("Authorization") String token,
            @Path("roleId") long roleId
    );
}
