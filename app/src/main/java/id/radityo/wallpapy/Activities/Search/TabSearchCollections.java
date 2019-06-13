package id.radityo.wallpapy.Activities.Search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import static id.radityo.wallpapy.Activities.Search.SearchActivity.QUERY;
import static id.radityo.wallpapy.Constants.CLIENT_ID;
import static id.radityo.wallpapy.MyFragment.New.FragmentNew.TAG;

public class TabSearchCollections extends Fragment {
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    LinearLayout layoutOffline, linearSearch;
    ProgressBar progressBar;

    CollectionsAdapter collectionsAdapter;
    List<Collections> collectionsList = new ArrayList<>();

    String query;
    EndlessOnScrollListener endlessScrollListener;
    SearchActivity activity;

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_tab_photos);
        layoutOffline = view.findViewById(R.id.linear_internet_tab_photos);
        linearSearch = view.findViewById(R.id.linear_search_tab_photos);
        swipeRefresh = view.findViewById(R.id.refresh_tab_photos);
        progressBar = view.findViewById(R.id.progress_tab_photos);

        progressBar.setVisibility(View.GONE);
        layoutOffline.setVisibility(View.GONE);
        linearSearch.setVisibility(View.VISIBLE);
        swipeRefresh.setEnabled(false);
    }

    private void initRecyclerView(ViewGroup container) {
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setPadding(0, 16, 0, 0);

        collectionsAdapter = new CollectionsAdapter(collectionsList, activity);
        recyclerView.setAdapter(collectionsAdapter);

        endlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                searchCollections(CLIENT_ID, query, page);
            }
        };

        recyclerView.addOnScrollListener(endlessScrollListener);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_photos, container, false);

        initView(view);

        initRecyclerView(container);

//        searchCollections(CLIENT_ID, query, 1, view, container);

//        pullToRefresh(CLIENT_ID, query, view, container);

//        infiniteScroll(CLIENT_ID, query, view, container);

        return view;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(QUERY)) {

                query = intent.getStringExtra(QUERY);

                Log.e(TAG, "### TSC | receive query: " + query);

                endlessScrollListener.resetState();
                collectionsList.clear();
                collectionsAdapter.notifyDataSetChanged();

                linearSearch.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                searchCollections(CLIENT_ID, query, 1);

                pullToRefresh(CLIENT_ID, query);

//                infiniteScroll(CLIENT_ID, query);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SearchActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: TSC");
        activity.registerReceiver(broadcastReceiver, new IntentFilter(QUERY));
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unregisterReceiver(broadcastReceiver);
    }

    private void searchCollections(final String clientId, final String query, final int page) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.searchCollections(clientId, query, page, 15);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        swipeRefresh.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        linearSearch.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        JSONObject rootObject = new JSONObject(response.body().string());
                        JSONArray array = rootObject.getJSONArray("results");

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject collectionsObject = array.getJSONObject(i);

                            long id = collectionsObject.getLong("id");
                            String title = collectionsObject.getString("title");
                            String description = collectionsObject.getString("description");
                            String published_at = collectionsObject.getString("published_at");
                            String updated_at = collectionsObject.getString("updated_at");
                            int total_photos = collectionsObject.getInt("total_photos");
                            String share_key = collectionsObject.getString("share_key");

                            Collections collections = new Collections();

                            collections.setId((int) id);
                            collections.setTitle(title);
                            collections.setDescription(description);
                            collections.setPublishedAt(published_at);
                            collections.setUpdatedAt(updated_at);
                            collections.setTotalPhotos(total_photos);
                            collections.setShareKey(share_key);

                            // USER
                            JSONObject userObject = collectionsObject.getJSONObject("user");

                            String user_id = userObject.getString("id");
                            String user_updated_at = userObject.getString("updated_at");
                            String username = userObject.getString("username");
                            String name = userObject.getString("name");
                            String bio = userObject.getString("bio");
                            String location = userObject.getString("location");
                            String instagram_username = userObject.getString("instagram_username");
                            String total_collections = userObject.getString("total_collections");
                            String total_likes = userObject.getString("total_likes");
                            String user_total_photos = userObject.getString("total_photos");

                            // USER LINKS
                            JSONObject linksUserObject = userObject.getJSONObject("links");

                            String self = linksUserObject.getString("self");
                            String html = linksUserObject.getString("html");
                            String photos = linksUserObject.getString("photos");
                            String likes = linksUserObject.getString("likes");
                            String portfolio = linksUserObject.getString("portfolio");
                            String following = linksUserObject.getString("following");
                            String followers = linksUserObject.getString("followers");

                            //USER PROFILE IMAGE
                            JSONObject profileImageObject = userObject.getJSONObject("profile_image");

                            String small = profileImageObject.getString("small");
                            String medium = profileImageObject.getString("medium");
                            String large = profileImageObject.getString("large");


                            AuthorProfile authorProfile = new AuthorProfile();
                            authorProfile.setMedium(medium);

                            Author author = new Author();
                            author.setId(user_id);
                            author.setName(name);
                            author.setUsername(username);
                            author.setAuthorProfile(authorProfile);


                            // COVER PHOTO
                            JSONObject coverPhotoObject = collectionsObject.getJSONObject("cover_photo");

                            String cover_id = coverPhotoObject.getString("id");
                            int width = coverPhotoObject.getInt("width");
                            int height = coverPhotoObject.getInt("height");
                            String color = coverPhotoObject.getString("color");
                            String description_cover = coverPhotoObject.getString("description");

                            // COVER URLS
                            JSONObject coverPhotoUrlsObject = coverPhotoObject.getJSONObject("urls");
                            String regular_cover = coverPhotoUrlsObject.getString("regular");

                            // COVER LINKS
                            JSONObject coverPhotoLinksObject = coverPhotoObject.getJSONObject("links");

                            String cover_links_self = coverPhotoLinksObject.getString("self");
                            String cover_links_html = coverPhotoLinksObject.getString("html");
                            String cover_links_download = coverPhotoLinksObject.getString("download");
                            String cover_links_download_location = coverPhotoLinksObject.getString("download_location");


                            // ----- SET COVER ----- //
                            CoverPhotoUrls coverPhotoUrls = new CoverPhotoUrls();
                            coverPhotoUrls.setRegular(regular_cover);

                            CoverPhoto coverPhoto = new CoverPhoto();
                            coverPhoto.setId(cover_id);
                            coverPhoto.setUrls(coverPhotoUrls);

                            collections.setCoverPhoto(coverPhoto);
                            collections.setAuthor(author);


                            // PREVIEW PHOTO
                            JSONArray previewArray = collectionsObject.getJSONArray("preview_photos");
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
                            linearSearch.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
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

                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    linearSearch.setVisibility(View.VISIBLE);
                    swipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, query);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Tab Collections");
                t.printStackTrace();

                Toast.makeText(activity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                linearSearch.setVisibility(View.VISIBLE);
                swipeRefresh.setRefreshing(false);

                pullToRefresh(clientId, query);
            }
        });
    }

    private void pullToRefresh(final String clientId, final String query) {
        swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                endlessScrollListener.resetState();
                collectionsList.clear();
                collectionsAdapter.notifyDataSetChanged();

                searchCollections(clientId, query, 1);
//                infiniteScroll(clientId, query);
            }
        });
    }

//    private void infiniteScroll(final String clientId, final String query) {
//        recyclerView.addOnScrollListener(new EndlessOnScrollListener() {
//            @Override
//            public void onLoadMore(int page) {
//                searchCollections(clientId, query, page);
//            }
//        });
//    }

}
