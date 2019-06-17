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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.radityo.wallpapy.Fragments.New.Model.Author.Author;
import id.radityo.wallpapy.Fragments.New.Model.New;
import id.radityo.wallpapy.Fragments.New.Model.Urls;
import id.radityo.wallpapy.Fragments.New.NewAdapter;
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

public class TabSearchPhotos extends Fragment {
    private RecyclerView mRecyclerView;
    LinearLayout mLayoutNetwork;
    private LinearLayout mLinearSearch;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mProgressBar;

    private EndlessOnScrollListener mEndlessScrollListener;
    private SearchActivity mActivity;
    private List<New> mNewList = new ArrayList<>();
    private NewAdapter mNewAdapter;

    private String mQuery;

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_tab_photos);
//        mLayoutNetwork = view.findViewById(R.id.search_layout_tab_photos);
        mSwipeRefresh = view.findViewById(R.id.refresh_tab_photos);
        mProgressBar = view.findViewById(R.id.progress_tab_photos);
        mLinearSearch = view.findViewById(R.id.search_layout_tab_photos);

//        mLayoutNetwork.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mLinearSearch.setVisibility(View.VISIBLE);
        mSwipeRefresh.setEnabled(false);
    }

    private void initRecyclerView(ViewGroup container) {
        mRecyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(false);

        mNewAdapter = new NewAdapter(mActivity, mNewList);
        mRecyclerView.setAdapter(mNewAdapter);
        mEndlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                searchPhotos(CLIENT_ID, mQuery, page);
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
                Log.e(TAG, "### TSP | receive query: " + mQuery);

                mEndlessScrollListener.resetState();
                mNewList.clear();
                mNewAdapter.notifyDataSetChanged();

                mLinearSearch.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                searchPhotos(CLIENT_ID, mQuery, 1);

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

    private void searchPhotos(final String clientId, final String query, final int page) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.searchPhotos(clientId, query, page, 20);
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

                            mNewList.add(unew);
                        }

                        mNewAdapter.notifyDataSetChanged();

                        if (mNewList.isEmpty()) {
                            Log.e(TAG, "EMPTY: " + mNewList.size());
                            mLinearSearch.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e(TAG, "onResponseNotSuccessful: Tab Search Photos");
                    Toast.makeText(
                            mActivity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    mRecyclerView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mLinearSearch.setVisibility(View.VISIBLE);
                    mSwipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, query);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Tab Search Photos");
                t.printStackTrace();

                Toast.makeText(mActivity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mLinearSearch.setVisibility(View.VISIBLE);
                mSwipeRefresh.setRefreshing(false);

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
                mNewList.clear();
                mNewAdapter.notifyDataSetChanged();

                searchPhotos(clientId, query, 1);
            }
        });
    }
}
