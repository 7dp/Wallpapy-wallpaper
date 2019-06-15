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

import id.radityo.wallpapy.Fragments.Collections.CollectionsAdapter;
import id.radityo.wallpapy.Fragments.Collections.Model.Author.Author;
import id.radityo.wallpapy.Fragments.Collections.Model.Author.AuthorProfile;
import id.radityo.wallpapy.Fragments.Collections.Model.Collections;
import id.radityo.wallpapy.Fragments.Collections.Model.CoverPhoto.CoverPhoto;
import id.radityo.wallpapy.Fragments.Collections.Model.CoverPhoto.CoverPhotoUrls;
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

public class TabSearchCollections extends Fragment {
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    LinearLayout mLayoutNetwork;
    private LinearLayout mLayoutSearch;
    private ProgressBar mProgressBar;

    private EndlessOnScrollListener mEndlessScrollListener;
    private SearchActivity mActivity;
    private List<Collections> mCollectionList = new ArrayList<>();
    private CollectionsAdapter mCollectionAdapter;

    private String mQuery;

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_tab_photos);
        mLayoutNetwork = view.findViewById(R.id.linear_internet_tab_photos);
        mLayoutSearch = view.findViewById(R.id.linear_search_tab_photos);
        mSwipeRefresh = view.findViewById(R.id.refresh_tab_photos);
        mProgressBar = view.findViewById(R.id.progress_tab_photos);

        mProgressBar.setVisibility(View.GONE);
        mLayoutNetwork.setVisibility(View.GONE);
        mLayoutSearch.setVisibility(View.VISIBLE);
        mSwipeRefresh.setEnabled(false);
    }

    private void initRecyclerView(ViewGroup container) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(false);
        mRecyclerView.setPadding(0, 16, 0, 0);

        mCollectionAdapter = new CollectionsAdapter(mActivity, mCollectionList);
        mRecyclerView.setAdapter(mCollectionAdapter);

        mEndlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                searchCollections(CLIENT_ID, mQuery, page);
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
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

                Log.e(TAG, "### TSC | receive query: " + mQuery);

                mEndlessScrollListener.resetState();
                mCollectionList.clear();
                mCollectionAdapter.notifyDataSetChanged();

                mLayoutSearch.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                searchCollections(CLIENT_ID, mQuery, 1);

                pullToRefresh(CLIENT_ID, mQuery);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (SearchActivity) getActivity();
    }

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

    private void searchCollections(final String clientId, final String query, final int page) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.searchCollections(clientId, query, page, 15);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {

                        mSwipeRefresh.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);
                        mLayoutSearch.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);

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
                            String username = userObject.getString("username");
                            String name = userObject.getString("name");

                            //USER PROFILE IMAGE
                            JSONObject profileImageObject = userObject.getJSONObject("profile_image");
                            String medium = profileImageObject.getString("medium");
                            String large = profileImageObject.getString("large");

                            AuthorProfile authorProfile = new AuthorProfile();
                            authorProfile.setMedium(medium);
                            authorProfile.setLarge(large);

                            Author author = new Author();
                            author.setId(user_id);
                            author.setName(name);
                            author.setUsername(username);
                            author.setAuthorProfile(authorProfile);

                            // COVER PHOTO
                            JSONObject coverPhotoObject = collectionsObject.getJSONObject("cover_photo");
                            String cover_id = coverPhotoObject.getString("id");

                            // COVER URLS
                            JSONObject coverPhotoUrlsObject = coverPhotoObject.getJSONObject("urls");
                            String regular_cover = coverPhotoUrlsObject.getString("regular");

                            // SET COVER
                            CoverPhotoUrls coverPhotoUrls = new CoverPhotoUrls();
                            coverPhotoUrls.setRegular(regular_cover);

                            CoverPhoto coverPhoto = new CoverPhoto();
                            coverPhoto.setId(cover_id);
                            coverPhoto.setUrls(coverPhotoUrls);

                            collections.setCoverPhoto(coverPhoto);
                            collections.setAuthor(author);

                            mCollectionList.add(collections);
                        }

                        mCollectionAdapter.notifyDataSetChanged();

                        if (mCollectionList.isEmpty()) {
                            Log.e(TAG, "EMPTY: " + mCollectionList.size());
                            mRecyclerView.setVisibility(View.GONE);
                            mLayoutSearch.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e(TAG, "onResponseNotSuccessful: Tab Collections");

                    Toast.makeText(mActivity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    mProgressBar.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    mLayoutSearch.setVisibility(View.VISIBLE);
                    mSwipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, query);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Tab Collections");
                t.printStackTrace();

                Toast.makeText(mActivity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mLayoutSearch.setVisibility(View.VISIBLE);
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
                mCollectionList.clear();
                mCollectionAdapter.notifyDataSetChanged();

                searchCollections(clientId, query, 1);
            }
        });
    }
}
