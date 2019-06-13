package id.radityo.wallpapy.Activities.DetailAuthor;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.radityo.wallpapy.MyFragment.Collections.CollectionsAdapter;
import id.radityo.wallpapy.MyFragment.Collections.Model.Author.Author;
import id.radityo.wallpapy.MyFragment.Collections.Model.Author.AuthorProfile;
import id.radityo.wallpapy.MyFragment.Collections.Model.Collections;
import id.radityo.wallpapy.MyFragment.Collections.Model.CoverPhoto.CoverPhoto;
import id.radityo.wallpapy.MyFragment.Collections.Model.CoverPhoto.CoverPhotoUrls;
import id.radityo.wallpapy.R;
import id.radityo.wallpapy.Request.APIService;
import id.radityo.wallpapy.Request.ApiClient;
import id.radityo.wallpapy.Utils.EndlessOnScrollListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.radityo.wallpapy.Constants.CLIENT_ID;
import static id.radityo.wallpapy.MyFragment.New.FragmentNew.TAG;

public class TabCollections extends Fragment {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    LinearLayout layoutOffline;
    ProgressBar progressBar;
    TextView tvEmpty;

    CollectionsAdapter collectionsAdapter;
    List<Collections> collectionsList = new ArrayList<>();
    EndlessOnScrollListener endlessScrollListener = null;
    DetailAuthorActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DetailAuthorActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_photos, container, false);

        String username = ((DetailAuthorActivity) getActivity()).getUsername();
        Log.e(TAG, "username ocv: " + username);

        findViewById(view);

        initRecyclerView(container, username);

        requestUserCollections(CLIENT_ID, username, 1, container);

        pullToRefresh(CLIENT_ID, username, container);

//        infiniteScroll(CLIENT_ID, username, view, container);

        return view;
    }

    private void findViewById(View view) {
        recyclerView = view.findViewById(R.id.recycler_tab_photos);
        layoutOffline = view.findViewById(R.id.linear_internet_tab_photos);
        swipeRefresh = view.findViewById(R.id.refresh_tab_photos);
        progressBar = view.findViewById(R.id.progress_tab_photos);
        tvEmpty = view.findViewById(R.id.tv_empty_tab_photos);

        layoutOffline.setVisibility(View.GONE);
    }

    private void initRecyclerView(final ViewGroup container, final String username) {
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setMotionEventSplittingEnabled(false);

        collectionsAdapter = new CollectionsAdapter(collectionsList, getActivity());
        recyclerView.setAdapter(collectionsAdapter);
        recyclerView.setPadding(0, 16, 0, 0);

        endlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                requestUserCollections(CLIENT_ID, username, page, container);
            }
        };

        recyclerView.addOnScrollListener(endlessScrollListener);
    }

    private void requestUserCollections(
            final String clientId,
            final String username,
            final int page,
            final ViewGroup container) {

        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.getUserCollections(username, clientId, 15, page);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        swipeRefresh.setRefreshing(false);
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutOffline.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.GONE);

                        JSONArray array = new JSONArray(response.body().string());

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject rootObject = array.getJSONObject(i);

                            long id = rootObject.getLong("id");
                            String title = rootObject.getString("title");
                            String description = rootObject.getString("description");
                            String published_at = rootObject.getString("published_at");
                            String updated_at = rootObject.getString("updated_at");
                            int total_photos = rootObject.getInt("total_photos");
                            String share_key = rootObject.getString("share_key");

                            Collections collections = new Collections();

                            collections.setId((int) id);
                            collections.setTitle(title);
                            collections.setDescription(description);
                            collections.setPublishedAt(published_at);
                            collections.setUpdatedAt(updated_at);
                            collections.setTotalPhotos(total_photos);
                            collections.setShareKey(share_key);

                            // USER
                            JSONObject userObject = rootObject.getJSONObject("user");

                            String user_id = userObject.getString("id");
                            String username = userObject.getString("username");
                            String name = userObject.getString("name");
                            String bio = userObject.getString("bio");
                            String location = userObject.getString("location");

                            // USER LINKS
                            JSONObject linksUserObject = userObject.getJSONObject("links");

                            //USER PROFILE IMAGE
                            JSONObject profileImageObject = userObject.getJSONObject("profile_image");

                            String small = profileImageObject.getString("small");
                            String medium = profileImageObject.getString("medium");
                            String large = profileImageObject.getString("large");

                            // ----- SET USER ----- //

                            AuthorProfile authorProfile = new AuthorProfile();
                            authorProfile.setSmall(small);
                            authorProfile.setMedium(medium);
                            authorProfile.setLarge(large);

                            Author author = new Author();
                            author.setId(user_id);
                            author.setName(name);
                            author.setUsername(username);
                            author.setBio(bio);
                            author.setLocation(location);
                            author.setAuthorProfile(authorProfile);

                            // COVER PHOTO
                            JSONObject coverPhotoObject = rootObject.getJSONObject("cover_photo");
                            String cover_id = coverPhotoObject.getString("id");

                            // COVER URLS
                            JSONObject coverPhotoUrlsObject = coverPhotoObject.getJSONObject("urls");
                            String regular_cover = coverPhotoUrlsObject.getString("regular");

                            // COVER LINKS
                            JSONObject coverPhotoLinksObject = coverPhotoObject.getJSONObject("links");


                            // ----- SET COVER -----
                            CoverPhotoUrls coverPhotoUrls = new CoverPhotoUrls();
                            coverPhotoUrls.setRegular(regular_cover);

                            CoverPhoto coverPhoto = new CoverPhoto();
                            coverPhoto.setId(cover_id);
                            coverPhoto.setUrls(coverPhotoUrls);

                            collections.setCoverPhoto(coverPhoto);
                            collections.setAuthor(author);


                            // PREVIEW PHOTO
                            JSONArray previewArray = rootObject.getJSONArray("preview_photos");
                            for (int a = 0; a < previewArray.length(); a++) {

                                JSONObject previewObject = previewArray.getJSONObject(a);
                                String preview_id = previewObject.getString("id");

                                JSONObject previewUrlsObject = previewObject.getJSONObject("urls");
                                String preview_photo_regular = previewUrlsObject.getString("regular");
                            }

                            collectionsList.add(collections);
                        }

                        collectionsAdapter.notifyDataSetChanged();

                        if (collectionsList.isEmpty()) {
                            Log.e(TAG, "EMPTY: " + collectionsList.size());
                            tvEmpty.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    Log.e(TAG, "onResponseNotSuccessful: Tab Collections");
                    Toast.makeText(activity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    recyclerView.setVisibility(View.GONE);
                    layoutOffline.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, username, container);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Tab Collections");
                t.printStackTrace();

                Toast.makeText(activity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                layoutOffline.setVisibility(View.VISIBLE);
                swipeRefresh.setRefreshing(false);

                pullToRefresh(clientId, username, container);
            }
        });
    }

    private void pullToRefresh(final String clientId, final String username, final ViewGroup container) {
        swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                endlessScrollListener.resetState();
                collectionsList.clear();
                collectionsAdapter.notifyDataSetChanged();

                requestUserCollections(clientId, username, 1, container);
//                infiniteScroll(clientId, username, container);
            }
        });
    }

//    private void infiniteScroll(
//            final String clientId,
//            final String username,
//            final View view,
//            final ViewGroup container) {
//
//        recyclerView.addOnScrollListener(new EndlessOnScrollListener() {
//            @Override
//            public void onLoadMore(int page) {
//                requestUserCollections(clientId, username, page, view, container);
//            }
//        });
//    }
}
