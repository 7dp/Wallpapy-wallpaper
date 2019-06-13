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
import android.support.v7.widget.GridLayoutManager;
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

import id.radityo.wallpapy.MyFragment.New.Model.Author.Author;
import id.radityo.wallpapy.MyFragment.New.Model.New;
import id.radityo.wallpapy.MyFragment.New.Model.Urls;
import id.radityo.wallpapy.MyFragment.New.NewAdapter;
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

public class TabSearchPhotos extends Fragment {
    RecyclerView recyclerView;
    LinearLayout layoutOffline, linearSearch;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar progressBar;

    List<New> newList = new ArrayList<>();
    NewAdapter newAdapter;

    String query;
    EndlessOnScrollListener endlessScrollListener;
    SearchActivity activity;

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_tab_photos);
        layoutOffline = view.findViewById(R.id.linear_internet_tab_photos);
        swipeRefresh = view.findViewById(R.id.refresh_tab_photos);
        progressBar = view.findViewById(R.id.progress_tab_photos);
        linearSearch = view.findViewById(R.id.linear_search_tab_photos);

        layoutOffline.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        linearSearch.setVisibility(View.VISIBLE);
        swipeRefresh.setEnabled(false);
    }

    private void initRecyclerView(ViewGroup container) {
        recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setMotionEventSplittingEnabled(false);

        newAdapter = new NewAdapter(activity, newList);
        recyclerView.setAdapter(newAdapter);
        endlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                searchPhotos(CLIENT_ID, query, page);
            }
        };

        recyclerView.addOnScrollListener(endlessScrollListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SearchActivity) getActivity();
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

//        searchPhotos(CLIENT_ID, query, 1, view, container);

//        pullToRefresh(CLIENT_ID, query, view, container);

//        infiniteScroll(CLIENT_ID, query, view, container);

        return view;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(QUERY)) {

                query = intent.getStringExtra(QUERY);
                Log.e(TAG, "### TSP | receive query: " + query);

                endlessScrollListener.resetState();
                newList.clear();
                newAdapter.notifyDataSetChanged();

                linearSearch.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                searchPhotos(CLIENT_ID, query, 1);

                pullToRefresh(CLIENT_ID, query);

//                infiniteScroll(CLIENT_ID, query);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: TSP");
        activity.registerReceiver(broadcastReceiver, new IntentFilter(QUERY));
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unregisterReceiver(broadcastReceiver);
    }

    private void searchPhotos(final String clientId, final String query, final int page) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.searchPhotos(clientId, query, page, 20);
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

                            JSONObject photosObject = array.getJSONObject(i);

                            String id = photosObject.getString("id");
                            String createdAt = photosObject.getString("created_at");
                            String updatedAt = photosObject.getString("updated_at");
                            int width = photosObject.getInt("width");
                            int height = photosObject.getInt("height");
                            String color = photosObject.getString("color");
                            String description = photosObject.getString("description");
                            String alternateDescription = photosObject.getString("alt_description");

                            // URLS
                            JSONObject objUrls = photosObject.getJSONObject("urls");
                            String regular = objUrls.getString("regular");

                            // AUTHOR
                            JSONObject objAuthor = photosObject.getJSONObject("user");
                            String name = objAuthor.getString("name");

                            // author profile
                            JSONObject profileImage = objAuthor.getJSONObject("profile_image");
                            String profile_medium = profileImage.getString("medium");

                            Urls urls = new Urls();
                            urls.setRegular(regular);

                            Author author = new Author();
                            author.setName(name);

                            New unew = new New();
                            unew.setId(id);
                            unew.setCreatedAt(createdAt);
                            unew.setUpdatedAt(updatedAt);
                            unew.setWidth(width);
                            unew.setHeight(height);
                            unew.setColor(color);
                            unew.setDescription(description);
                            unew.setAltDescription(alternateDescription);

                            unew.setUrls(urls);
                            unew.setAuthor(author);

                            newList.add(unew);
                        }

                        newAdapter.notifyDataSetChanged();

                        if (newList.isEmpty()) {
                            Log.e(TAG, "EMPTY: " + newList.size());
                            linearSearch.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e(TAG, "onResponseNotSuccessful: Tab Search Photos");
                    Toast.makeText(
                            activity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    linearSearch.setVisibility(View.VISIBLE);
                    swipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, query);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Tab Search Photos");
                t.printStackTrace();

                Toast.makeText(activity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
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
                newList.clear();
                newAdapter.notifyDataSetChanged();

                searchPhotos(clientId, query, 1);
//                infiniteScroll(clientId, query);
            }
        });
    }

//    private void infiniteScroll(final String clientId, final String query) {
//        recyclerView.addOnScrollListener(new EndlessOnScrollListener() {
//            @Override
//            public void onLoadMore(int page) {
//                searchPhotos(clientId, query, page);
//            }
//        });
//    }

}
