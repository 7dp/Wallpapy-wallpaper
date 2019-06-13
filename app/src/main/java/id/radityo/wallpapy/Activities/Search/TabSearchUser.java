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

import id.radityo.wallpapy.Activities.DetailAuthor.User.ProfileImage;
import id.radityo.wallpapy.Activities.DetailAuthor.User.User;
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

public class TabSearchUser extends Fragment {
    RecyclerView recyclerView;
    LinearLayout layoutOffline, linearSearch;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar progressBar;

    List<User> userList = new ArrayList<>();
    UserAdapter userAdapter;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setPadding(0, 4, 0, 0);

        userAdapter = new UserAdapter(userList, activity);
        recyclerView.setAdapter(userAdapter);
        endlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                searchUsers(CLIENT_ID, page, query);
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

//        searchUsers(CLIENT_ID, 1, query, view, container);

//        pullToRefresh(CLIENT_ID, query, view, container);

//        infiniteScroll(CLIENT_ID, query, view, container);

        return view;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(QUERY)) {

                query = intent.getStringExtra(QUERY);
                Log.e(TAG, "### TSU | receive query: " + query);

                endlessScrollListener.resetState();
                userList.clear();
                userAdapter.notifyDataSetChanged();

                linearSearch.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                searchUsers(CLIENT_ID, 1, query);

                pullToRefresh(CLIENT_ID, query);

//                infiniteScroll(CLIENT_ID, query);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: TSU");
        activity.registerReceiver(broadcastReceiver, new IntentFilter(QUERY));
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unregisterReceiver(broadcastReceiver);
    }

    private void searchUsers(final String clientId, final int page, final String query) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.searchUsers(clientId, query, page, 20);
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
                        String _total = rootObject.getString("total");
                        String _totalPages = rootObject.getString("total_pages");

                        JSONArray array = rootObject.getJSONArray("results");

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject userObject = array.getJSONObject(i);

                            String _id = userObject.getString("id");
                            String _updatedAt = userObject.getString("updated_at");
                            String _username = userObject.getString("username");
                            String _name = userObject.getString("name");
                            String portfolioUrl = userObject.getString("portfolio_url");
                            String _bio = userObject.getString("bio");
                            String _location = userObject.getString("location");

                            // profile
                            JSONObject profileObject = userObject.getJSONObject("profile_image");
                            String _profileMedium = profileObject.getString("medium");
                            String _profileLarge = profileObject.getString("large");

                            ProfileImage profileImage = new ProfileImage();
                            profileImage.setMedium(_profileMedium);
                            profileImage.setLarge(_profileLarge);

                            User user = new User();
                            user.setId(_id);
                            user.setBio(_bio);
                            user.setUpdatedAt(_updatedAt);
                            user.setUsername(_username);
                            user.setName(_name);
                            user.setPortfolioUrl(portfolioUrl);
                            user.setLocation(_location);
                            user.setProfileImage(profileImage);

                            userList.add(user);
                        }

                        userAdapter.notifyDataSetChanged();

                        if (userList.isEmpty()) {
                            Log.e(TAG, "EMPTY: " + userList.size());
                            linearSearch.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e(TAG, "onResponseNotSuccessful: Tab User");
                    Toast.makeText(activity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    swipeRefresh.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    linearSearch.setVisibility(View.VISIBLE);

                    pullToRefresh(clientId, query);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Tab User");
                t.printStackTrace();

                Toast.makeText(activity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                swipeRefresh.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                linearSearch.setVisibility(View.VISIBLE);

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
                userList.clear();
                userAdapter.notifyDataSetChanged();

                searchUsers(clientId, 1, query);
//                infiniteScroll(clientId, query);
            }
        });
    }

//    private void infiniteScroll(final String clientId, final String query) {
//        recyclerView.addOnScrollListener(new EndlessOnScrollListener() {
//            @Override
//            public void onLoadMore(int page) {
//                Log.e(TAG, "onLoadMore: page: " + page);
//                searchUsers(clientId, page, query);
//            }
//        });
//    }

}
