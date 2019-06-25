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

import static id.radityo.wallpapy.Fragments.New.FragmentNew.TAG;
import static id.radityo.wallpapy.Utils.Cons.CLIENT_ID;

public class TabCollections extends Fragment {
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private LinearLayout mLayoutNetwork;
    private ProgressBar mProgressBar;
    private TextView mTvEmpty;

    private EndlessOnScrollListener mEndlessScrollListener = null;
    private DetailAuthorActivity mActivity;
    private List<Collections> mCollectionsList = new ArrayList<>();
    private CollectionsAdapter mCollectionsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (DetailAuthorActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_photos, container, false);

        String username = mActivity.getUsername();

        initView(view);

        initRecyclerView(container, username);

        requestUserCollections(CLIENT_ID, username, 1, container);

        pullToRefresh(CLIENT_ID, username, container);

        return view;
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_tab_photos);
        mLayoutNetwork = view.findViewById(R.id.search_layout_tab_photos);
        mSwipeRefresh = view.findViewById(R.id.refresh_tab_photos);
        mProgressBar = view.findViewById(R.id.progress_tab_photos);
        mTvEmpty = view.findViewById(R.id.tv_empty_tab_photos);

        mLayoutNetwork.setVisibility(View.GONE);
    }

    private void initRecyclerView(final ViewGroup container, final String username) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(false);

        mCollectionsAdapter = new CollectionsAdapter(mActivity, mCollectionsList);
        mRecyclerView.setAdapter(mCollectionsAdapter);
        mRecyclerView.setPadding(0, 16, 0, 0);

        mEndlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                requestUserCollections(CLIENT_ID, username, page, container);
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
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
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {

                        mSwipeRefresh.setRefreshing(false);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLayoutNetwork.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                        mTvEmpty.setVisibility(View.GONE);

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

                            mCollectionsList.add(collections);
                        }

                        mCollectionsAdapter.notifyDataSetChanged();

                        if (mCollectionsList.isEmpty()) {
                            Log.e(TAG, "EMPTY: " + mCollectionsList.size());
                            mTvEmpty.setVisibility(View.VISIBLE);
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

                    mRecyclerView.setVisibility(View.GONE);
                    mLayoutNetwork.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, username, container);
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

                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mLayoutNetwork.setVisibility(View.VISIBLE);
                mSwipeRefresh.setRefreshing(false);

                pullToRefresh(clientId, username, container);
            }
        });
    }

    private void pullToRefresh(final String clientId, final String username, final ViewGroup container) {
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEndlessScrollListener.resetState();
                mCollectionsList.clear();
                mCollectionsAdapter.notifyDataSetChanged();

                requestUserCollections(clientId, username, 1, container);
            }
        });
    }
}
