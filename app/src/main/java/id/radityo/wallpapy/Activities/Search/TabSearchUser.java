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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
import static id.radityo.wallpapy.Fragments.New.FragmentNew.TAG;
import static id.radityo.wallpapy.Utils.Cons.CLIENT_ID;

public class TabSearchUser extends Fragment {
    private RecyclerView mRecyclerView;
    LinearLayout mLayoutNetwork;
    private LinearLayout mLinearSearch;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mProgressBar;

    private EndlessOnScrollListener mEndlessScrollListener = null;
    private SearchActivity mActivity;
    private List<User> mUserList = new ArrayList<>();
    private UserAdapter mUserAdapter;

    private String mQuery;

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_tab_photos);
        mLayoutNetwork = view.findViewById(R.id.search_layout_tab_photos);
        mSwipeRefresh = view.findViewById(R.id.refresh_tab_photos);
        mProgressBar = view.findViewById(R.id.progress_tab_photos);
        mLinearSearch = view.findViewById(R.id.linear_search_tab_photos);

        mLayoutNetwork.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mLinearSearch.setVisibility(View.VISIBLE);
        mSwipeRefresh.setEnabled(false);
    }

    private void initRecyclerView(ViewGroup container) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(false);
        mRecyclerView.setPadding(0, 4, 0, 0);

        mUserAdapter = new UserAdapter(mActivity, mUserList);
        mRecyclerView.setAdapter(mUserAdapter);
        mEndlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                searchUsers(CLIENT_ID, page, mQuery);
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (SearchActivity) getActivity();
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

        return view;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(QUERY)) {

                mQuery = intent.getStringExtra(QUERY);
                Log.e(TAG, "### TSU | receive query: " + mQuery);

                mEndlessScrollListener.resetState();
                mUserList.clear();
                mUserAdapter.notifyDataSetChanged();

                mLinearSearch.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                searchUsers(CLIENT_ID, 1, mQuery);

                pullToRefresh(CLIENT_ID, mQuery);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mActivity.registerReceiver(broadcastReceiver, new IntentFilter(QUERY));
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.unregisterReceiver(broadcastReceiver);
    }

    private void searchUsers(final String clientId, final int page, final String query) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.searchUsers(clientId, query, page, 20);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        mSwipeRefresh.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        mLinearSearch.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);

                        JSONObject rootObject = new JSONObject(response.body().string());
                        JSONArray array = rootObject.getJSONArray("results");

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject userObject = array.getJSONObject(i);
                            String id = userObject.getString("id");
                            String updatedAt = userObject.getString("updated_at");
                            String username = userObject.getString("username");
                            String name = userObject.getString("name");
                            String portfolioUrl = userObject.getString("portfolio_url");
                            String bio = userObject.getString("bio");
                            String location = userObject.getString("location");

                            // profile
                            JSONObject profileObject = userObject.getJSONObject("profile_image");
                            String profileMedium = profileObject.getString("medium");
                            String profileLarge = profileObject.getString("large");

                            ProfileImage profileImage = new ProfileImage();
                            profileImage.setMedium(profileMedium);
                            profileImage.setLarge(profileLarge);

                            User user = new User();
                            user.setId(id);
                            user.setBio(bio);
                            user.setUpdatedAt(updatedAt);
                            user.setUsername(username);
                            user.setName(name);
                            user.setPortfolioUrl(portfolioUrl);
                            user.setLocation(location);
                            user.setProfileImage(profileImage);

                            mUserList.add(user);
                        }

                        mUserAdapter.notifyDataSetChanged();

                        if (mUserList.isEmpty()) {
                            Log.e(TAG, "EMPTY: " + mUserList.size());
                            mLinearSearch.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e(TAG, "onResponseNotSuccessful: Tab User");
                    Toast.makeText(mActivity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    mSwipeRefresh.setRefreshing(false);
                    mRecyclerView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mLinearSearch.setVisibility(View.VISIBLE);

                    pullToRefresh(clientId, query);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Tab User");
                t.printStackTrace();

                Toast.makeText(mActivity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                mSwipeRefresh.setRefreshing(false);
                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mLinearSearch.setVisibility(View.VISIBLE);

                pullToRefresh(clientId, query);
            }
        });
    }

    private void pullToRefresh(final String clientId, final String query) {
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEndlessScrollListener.resetState();
                mUserList.clear();
                mUserAdapter.notifyDataSetChanged();

                searchUsers(clientId, 1, query);
            }
        });
    }
}
