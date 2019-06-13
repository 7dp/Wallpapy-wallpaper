package id.radityo.wallpapy.MyFragment.Collections;

import android.content.Intent;
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

import static id.radityo.wallpapy.Constants.ALL;
import static id.radityo.wallpapy.Constants.CLIENT_ID;
import static id.radityo.wallpapy.Constants.CURATED;
import static id.radityo.wallpapy.Constants.FEATURED;
import static id.radityo.wallpapy.MyFragment.New.FragmentNew.TAG;

public class FragmentCollections extends Fragment {

    RecyclerView recyclerView;
    LinearLayout linearLayout;
    ProgressBar progressBar;
    SwipeRefreshLayout refreshLayout;

    CollectionsAdapter collectionsAdapter;
    List<Collections> collectionsList = new ArrayList<>();
    EndlessOnScrollListener endlessScrollListener = null;

    private MainActivity activity;
    private String sortby = ALL;

    private void initViewById(View view) {
        recyclerView = view.findViewById(R.id.recycler_collections);
        linearLayout = view.findViewById(R.id.linear_internet_collections);
        progressBar = view.findViewById(R.id.progress_collections);
        refreshLayout = view.findViewById(R.id.refresh_collections);

        recyclerView.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setHasFixedSize(true);
        collectionsAdapter = new CollectionsAdapter(collectionsList, getActivity());
        recyclerView.setAdapter(collectionsAdapter);

        endlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                Log.e(TAG, "onLoadMore: page -> " + page);
                requestCollections(CLIENT_ID, page, sortby);
            }
        };

        recyclerView.addOnScrollListener(endlessScrollListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
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

        requestCollections(CLIENT_ID, 1, sortby);

        pullToRefresh(CLIENT_ID, sortby);

//        infiniteScroll(CLIENT_ID, ALL);

        return view;
    }

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

        Log.e(TAG, "CALL: " + call);
        if (call != null)
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                    if (response.isSuccessful()) {

                        try {
                            recyclerView.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            refreshLayout.setRefreshing(false);

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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {

                        recyclerView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false);

                        pullToRefresh(clientId, orderBy);

                        Log.e(TAG, "onResponseNotSuccessful: FragmentCollections");
                        Toast.makeText(activity,
                                getString(R.string.server_error),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: FragmentCollections");
                    t.printStackTrace();

                    Toast.makeText(activity,
                            getString(R.string.no_internet),
                            Toast.LENGTH_SHORT)
                            .show();

                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    refreshLayout.setRefreshing(false);

                    pullToRefresh(clientId, orderBy);
                }
            });
    }

    private void pullToRefresh(final String clientId, final String orderBy) {
        refreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.GONE);

                endlessScrollListener.resetState();
                collectionsList.clear();
                collectionsAdapter.notifyDataSetChanged();

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
                Intent search = new Intent(activity, SearchActivity.class);
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
        sortby = sortBy;
        Log.e(TAG, "sortby: " + sortby);
        Log.e(TAG, "SORTBY: " + sortBy);

        LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
        llm.scrollToPositionWithOffset(0, 0);

        endlessScrollListener.resetState();
        collectionsList.clear();
        collectionsAdapter.notifyDataSetChanged();

        recyclerView.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        requestCollections(CLIENT_ID, 1, sortby);
        pullToRefresh(CLIENT_ID, sortby);
    }
}
