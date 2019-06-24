package id.radityo.wallpapy.Fragments.Collections;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import id.radityo.wallpapy.Activities.MainActivity;
import id.radityo.wallpapy.Activities.Search.SearchActivity;
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

import static id.radityo.wallpapy.Fragments.New.FragmentNew.TAG;
import static id.radityo.wallpapy.Utils.Cons.ALL;
import static id.radityo.wallpapy.Utils.Cons.BROADCAST_COLLECTION;
import static id.radityo.wallpapy.Utils.Cons.CLIENT_ID;
import static id.radityo.wallpapy.Utils.Cons.CURATED;
import static id.radityo.wallpapy.Utils.Cons.FEATURED;

public class FragmentCollections extends Fragment {

    RecyclerView mRecyclerView;
    LinearLayout mLinearLayout;
    ProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeRefresh;

    CollectionsAdapter mCollectionAdapter;
    List<Collections> mCollectionList = new ArrayList<>();
    EndlessOnScrollListener mEndlessScrollListener = null;

    private MainActivity mActivity;
    private String mSortBy = ALL;

    private void initViewById(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_collections);
        mLinearLayout = view.findViewById(R.id.offline_fragment_collection);
        mProgressBar = view.findViewById(R.id.progress_collections);
        mSwipeRefresh = view.findViewById(R.id.refresh_collections);

        mRecyclerView.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setMotionEventSplittingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mCollectionAdapter = new CollectionsAdapter(mActivity, mCollectionList);
        mRecyclerView.setAdapter(mCollectionAdapter);

        mEndlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                requestCollections(CLIENT_ID, page, mSortBy);
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_collections, container, false);

        initViewById(view);

        initRecyclerView();

        requestCollections(CLIENT_ID, 1, mSortBy);

        pullToRefresh(CLIENT_ID, mSortBy);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.registerReceiver(mBroadcastReceiver, new IntentFilter(BROADCAST_COLLECTION));
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(BROADCAST_COLLECTION)) {
                LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                llm.scrollToPositionWithOffset(0, 0);
                mRecyclerView.stopScroll();
            }
        }
    };

    private void requestCollections(final String clientId, final int page, final String orderBy) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call;
        Log.e(TAG, "orderBy: " + orderBy);

        switch (orderBy) {
            case ALL:
                call = service.getAllCollections(clientId, page, 20);
                break;
            case CURATED:
                call = service.getCuratedCollections(clientId, page, 20);
                break;
            case FEATURED:
                call = service.getFeaturedCollections(clientId, page, 20);
                break;
            default:
                call = null;
                break;
        }

        if (call != null) {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (response.isSuccessful()) {

                        try {
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mLinearLayout.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                            mSwipeRefresh.setRefreshing(false);

                            JSONArray rootArray = new JSONArray(response.body().string());

                            for (int i = 0; i < rootArray.length(); i++) {

                                JSONObject rootObject = rootArray.getJSONObject(i);

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

                                //USER PROFILE IMAGE
                                JSONObject profileImageObject = userObject.getJSONObject("profile_image");
                                String small = profileImageObject.getString("small");
                                String medium = profileImageObject.getString("medium");
                                String large = profileImageObject.getString("large");

                                // SET USER
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {

                        mRecyclerView.setVisibility(View.GONE);
                        mLinearLayout.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                        mSwipeRefresh.setRefreshing(false);

                        pullToRefresh(clientId, orderBy);

                        Log.e(TAG, "onResponseNotSuccessful: FragmentCollections");
                        Toast.makeText(mActivity,
                                getString(R.string.server_error),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: FragmentCollections");
                    t.printStackTrace();

                    Toast.makeText(mActivity,
                            getString(R.string.no_internet),
                            Toast.LENGTH_SHORT)
                            .show();

                    mRecyclerView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mSwipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, orderBy);
                }
            });
        }
    }

    private void pullToRefresh(final String clientId, final String orderBy) {
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mLinearLayout.setVisibility(View.GONE);

                mEndlessScrollListener.resetState();
                mCollectionList.clear();
                mCollectionAdapter.notifyDataSetChanged();

                requestCollections(clientId, 1, orderBy);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.collections_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_collections:
                Intent search = new Intent(mActivity, SearchActivity.class);
                startActivity(search);
                break;
            case R.id.action_sort_collections_all:
                sortPhotosBy(ALL);
                break;
            case R.id.action_sort_collections_curated:
                sortPhotosBy(CURATED);
                break;
            case R.id.action_sort_collections_featured:
                sortPhotosBy(FEATURED);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortPhotosBy(final String sortBy) {
        mSortBy = sortBy;
        Log.e(TAG, "mSortBy: " + mSortBy);
        Log.e(TAG, "SORTBY: " + sortBy);

        LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        llm.scrollToPositionWithOffset(0, 0);

        mEndlessScrollListener.resetState();
        mCollectionList.clear();
        mCollectionAdapter.notifyDataSetChanged();

        mRecyclerView.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        requestCollections(CLIENT_ID, 1, mSortBy);
        pullToRefresh(CLIENT_ID, mSortBy);
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
