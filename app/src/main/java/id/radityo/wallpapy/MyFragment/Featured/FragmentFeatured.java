package id.radityo.wallpapy.MyFragment.Featured;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import id.radityo.wallpapy.MyFragment.Featured.Model.Author.Author;
import id.radityo.wallpapy.MyFragment.Featured.Model.Author.AuthorLinks;
import id.radityo.wallpapy.MyFragment.Featured.Model.Author.AuthorProfile;
import id.radityo.wallpapy.MyFragment.Featured.Model.Featured;
import id.radityo.wallpapy.MyFragment.Featured.Model.Links;
import id.radityo.wallpapy.MyFragment.Featured.Model.Urls;
import id.radityo.wallpapy.R;
import id.radityo.wallpapy.Request.APIService;
import id.radityo.wallpapy.Request.ApiClient;
import id.radityo.wallpapy.Utils.EndlessOnScrollListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.radityo.wallpapy.Constants.CLIENT_ID;
import static id.radityo.wallpapy.Constants.LATEST_NEW;
import static id.radityo.wallpapy.Constants.OLDEST_NEW;
import static id.radityo.wallpapy.Constants.POPULAR_NEW;

public class FragmentFeatured extends Fragment {

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    LinearLayout linearLayout;
    ProgressBar progressBar;

    List<Featured> featuredList = new ArrayList<>();
    FeaturedAdapter featuredAdapter;
    EndlessOnScrollListener endlessScrollListener = null;

    private static final String TAG = "wallpapy";
    private String sortby = LATEST_NEW;
    private MainActivity activity;

    private void initViewById(View view) {
        refreshLayout = view.findViewById(R.id.refresh_random);
        recyclerView = view.findViewById(R.id.recycler_random);
        linearLayout = view.findViewById(R.id.linear_internet_random);
        progressBar = view.findViewById(R.id.progress_random);

        linearLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setMotionEventSplittingEnabled(false);
        featuredAdapter = new FeaturedAdapter(featuredList, activity);
        recyclerView.setAdapter(featuredAdapter);

        endlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                Log.e(TAG, "onLoadMore: page -> " + page);
                requestCuratedPhotos(CLIENT_ID, page, sortby);
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
        View view = inflater.inflate(R.layout.fragment_featured, container, false);

        initViewById(view);

        initRecyclerView();

        requestCuratedPhotos(CLIENT_ID, 1, sortby);

        pullToRefresh(CLIENT_ID, sortby);

//        infiniteScroll(CLIENT_ID, LATEST_NEW);

        return view;
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sortBy = intent.getStringExtra("sort_by");
            Log.e(TAG, "intent.getAction: " + intent.getAction());
            Log.e(TAG, "sortBy: " + sortBy);

            switch (sortBy) {
                case LATEST_NEW:

                    featuredList.clear();
                    featuredAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.VISIBLE);

                    requestCuratedPhotos(CLIENT_ID, 1, LATEST_NEW);
                    pullToRefresh(CLIENT_ID, LATEST_NEW);
//                    infiniteScroll(CLIENT_ID, LATEST_NEW);
                    break;

                case OLDEST_NEW:

                    featuredList.clear();
                    featuredAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.VISIBLE);

                    requestCuratedPhotos(CLIENT_ID, 1, OLDEST_NEW);
                    pullToRefresh(CLIENT_ID, OLDEST_NEW);
//                    infiniteScroll(CLIENT_ID, OLDEST_NEW);
                    break;

                case POPULAR_NEW:

                    featuredList.clear();
                    featuredAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.VISIBLE);

                    requestCuratedPhotos(CLIENT_ID, 1, POPULAR_NEW);
                    pullToRefresh(CLIENT_ID, POPULAR_NEW);
//                    infiniteScroll(CLIENT_ID, POPULAR_NEW);
                    break;
            }
        }
    };

    private void requestCuratedPhotos(final String clientId, final int page, final String orderBy) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.getCuratedPhotos(clientId, page, 20, orderBy);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        linearLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        refreshLayout.setRefreshing(false);

                        JSONArray array = new JSONArray(response.body().string());

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject rootObject = array.getJSONObject(i);

                            String id = rootObject.getString("id");
                            String createdAt = rootObject.getString("created_at");
                            String updatedAt = rootObject.getString("updated_at");
                            int width = rootObject.getInt("width");
                            int height = rootObject.getInt("height");
                            String color = rootObject.getString("color");
                            String description = rootObject.getString("description");
                            String alternateDescription = rootObject.getString("alt_description");

                            boolean sponsored = rootObject.getBoolean("sponsored");
                            String sponsoredBy = rootObject.getString("sponsored_by");
                            int likes = rootObject.getInt("likes");
                            JSONArray arrayCategories = rootObject.getJSONArray("categories");
                            JSONArray arrayUserCollections = rootObject.getJSONArray("current_user_collections");

                            // URLS
                            JSONObject objUrls = rootObject.getJSONObject("urls");
                            String regular = objUrls.getString("regular");

                            // LINKS
                            JSONObject objLinks = rootObject.getJSONObject("links");
                            String self = objLinks.getString("self");
                            String download = objLinks.getString("download");
                            String downloadLocation = objLinks.getString("download_location");

                            // AUTHOR
                            JSONObject objAuthor = rootObject.getJSONObject("user");
                            String name = objAuthor.getString("name");
                            String user_id = objAuthor.getString("id");
                            String user_updated_at = objAuthor.getString("updated_at");
                            String username = objAuthor.getString("username");
                            String portfolio_url = objAuthor.getString("portfolio_url");
                            String bio = objAuthor.getString("bio");
                            String user_location = objAuthor.getString("location");
                            String instagram_username = objAuthor.getString("instagram_username");
                            int total_collections = objAuthor.getInt("total_collections");
                            int total_likes = objAuthor.getInt("total_likes");
                            int total_photos = objAuthor.getInt("total_photos");
                            boolean accepted_tos = objAuthor.getBoolean("accepted_tos");

                            // author links
                            JSONObject authorLinksObject = objAuthor.getJSONObject("links");
                            String user_self = authorLinksObject.getString("self");
                            String user_html = authorLinksObject.getString("html");
                            String user_photos = authorLinksObject.getString("photos");
                            String user_likes = authorLinksObject.getString("likes");
                            String user_portfolio = authorLinksObject.getString("portfolio");
                            String user_following = authorLinksObject.getString("following");
                            String user_followers = authorLinksObject.getString("followers");

                            // author profile
                            JSONObject profileImage = objAuthor.getJSONObject("profile_image");
                            String profile_small = profileImage.getString("small");


                            Urls urls = new Urls();
                            urls.setRegular(regular);

                            Links links = new Links();
                            links.setSelf(self);
                            links.setDownload(download);
                            links.setDownloadLocation(downloadLocation);


                            AuthorProfile authorProfile = new AuthorProfile();
                            authorProfile.setSmall(profile_small);


                            AuthorLinks authorLinks = new AuthorLinks();
                            authorLinks.setSelf(user_self);
                            authorLinks.setPortfolio(user_portfolio);
                            authorLinks.setHtml(user_html);
                            authorLinks.setPhotos(user_photos);
                            authorLinks.setLikes(user_likes);
                            authorLinks.setFollowing(user_following);
                            authorLinks.setFollowers(user_followers);

                            // author
                            Author author = new Author();
                            author.setName(name);
                            author.setId(user_id);
                            author.setUsername(username);
                            author.setUpdatedAt(user_updated_at);
                            author.setPortfolioUrl(portfolio_url);
                            author.setBio(bio);
                            author.setLocation(user_location);
                            author.setInstagramUsername(instagram_username);
                            author.setTotalCollections(total_collections);
                            author.setTotalLikes(total_likes);
                            author.setTotalPhotos(total_photos);
                            author.setAcceptedTos(accepted_tos);
                            author.setLinks(authorLinks);
                            author.setAuthorProfile(authorProfile);

                            Featured featured = new Featured();
                            featured.setId(id);
                            featured.setCreatedAt(createdAt);
                            featured.setUpdatedAt(updatedAt);
                            featured.setWidth(width);
                            featured.setHeight(height);
                            featured.setColor(color);
                            featured.setDescription(description);
                            featured.setAltDescription(alternateDescription);
                            featured.setSponsored(sponsored);
                            featured.setSponsoredBy(sponsoredBy);
                            featured.setLikes(likes);

                            featured.setUrls(urls);
                            featured.setLinks(links);
                            featured.setAuthor(author);

                            featuredList.add(featured);
                        }

                        featuredAdapter.notifyDataSetChanged();

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

                    Log.e(TAG, "onResponseNotSuccessful: FragmentFeatured");
                    Toast.makeText(activity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    pullToRefresh(clientId, orderBy);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: FragmentFeatured");
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
                linearLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);

                endlessScrollListener.resetState();
                featuredList.clear();
                featuredAdapter.notifyDataSetChanged();

                requestCuratedPhotos(clientId, 1, orderBy);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.featured_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_featured_featured:
                Intent search = new Intent(activity, SearchActivity.class);
                startActivity(search);
                break;
            case R.id.action_sort_latest_featured:
                sortPhotosBy(LATEST_NEW);
                break;
            case R.id.action_sort_oldest_featured:
                sortPhotosBy(OLDEST_NEW);
                break;
            case R.id.action_sort_popular_featured:
                sortPhotosBy(POPULAR_NEW);
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

        GridLayoutManager glm = (GridLayoutManager) recyclerView.getLayoutManager();
        glm.scrollToPositionWithOffset(0, 0);

        endlessScrollListener.resetState();
        featuredList.clear();
        featuredAdapter.notifyDataSetChanged();

        recyclerView.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        requestCuratedPhotos(CLIENT_ID, 1, sortby);
        pullToRefresh(CLIENT_ID, sortby);
    }
}
