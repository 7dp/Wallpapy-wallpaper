package id.radityo.wallpapy.Request;

import retrofit2.Retrofit;

public class ApiClient {
    private static final String BASE_URL = "https://api.unsplash.com/";
    private static final String DOWNLOAD_URL = "https://unsplash.com/photos/";

    public static APIService getBaseUrl() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        return retrofit.create(APIService.class);
    }
}
