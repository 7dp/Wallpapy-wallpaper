package id.radityo.wallpapy.Request;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface APIService {

    @GET("photos")
    Call<ResponseBody> getAllPhotos(
            @Query("client_id") String clientId,
            @Query("per_page") Integer perPage,
            @Query("page") Integer page,
            @Query("order_by") String orderBy);

    @GET("photos/curated")
    Call<ResponseBody> getCuratedPhotos(
            @Query("client_id") String clientId,
            @Query("page") Integer page,
            @Query("per_page") Integer perPage,
            @Query("order_by") String orderBy);

    @GET("collections/curated")
    Call<ResponseBody> getCuratedCollections(
            @Query("client_id") String clientId,
            @Query("page") int page,
            @Query("per_page") Integer perPage);

    @GET("collections/featured")
    Call<ResponseBody> getFeaturedCollections(
            @Query("client_id") String clientId,
            @Query("page") int page,
            @Query("per_page") Integer perPage);

    @GET("collections")
    Call<ResponseBody> getAllCollections(
            @Query("client_id") String clientId,
            @Query("page") int page,
            @Query("per_page") Integer perPage);

    @GET("photos/{id}")
    Call<ResponseBody> getDetailPhoto(
            @Path("id") String id,
            @Query("client_id") String clientId);

    @GET("{id}/download")
    @Streaming
    Call<ResponseBody> downloadFile(
            @Path("id") String id);

    @GET("collections/{id}/photos")
    Call<ResponseBody> getCollectionPhotos(
            @Path("id") long id,
            @Query("client_id") String clientId,
            @Query("per_page") Integer perPage,
            @Query("page") int page);

    @GET("search/users")
    Call<ResponseBody> searchUsers(
            @Query("client_id") String clientId,
            @Query("query") String query,
            @Query("page") int page,
            @Query("per_page") int perPage);

    @GET("search/photos")
    Call<ResponseBody> searchPhotos(
            @Query("client_id") String clientId,
            @Query("query") String query,
            @Query("page") int page,
            @Query("per_page") int perPage);

    @GET("search/collections")
    Call<ResponseBody> searchCollections(
            @Query("client_id") String clientId,
            @Query("query") String query,
            @Query("page") int page,
            @Query("per_page") int perPage);

    @GET("users/{username}/photos")
    Call<ResponseBody> getUserPhotos(
            @Path("username") String username,
            @Query("client_id") String clientId,
            @Query("per_page") Integer perPage,
            @Query("page") int page);

    @GET("users/{username}/likes")
    Call<ResponseBody> getUserLikes(
            @Path("username") String username,
            @Query("client_id") String clientId,
            @Query("per_page") Integer perPage,
            @Query("page") int page);

    @GET("users/{username}/collections")
    Call<ResponseBody> getUserCollections(
            @Path("username") String username,
            @Query("client_id") String clientId,
            @Query("per_page") Integer perPage,
            @Query("page") int page);
}
