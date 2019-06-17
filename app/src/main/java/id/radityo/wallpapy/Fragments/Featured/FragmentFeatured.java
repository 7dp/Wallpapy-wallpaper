package id.radityo.wallpapy.Fragments.Featured;

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
import id.radityo.wallpapy.Fragments.Featured.Model.Author.Author;
import id.radityo.wallpapy.Fragments.Featured.Model.Author.AuthorLinks;
import id.radityo.wallpapy.Fragments.Featured.Model.Author.AuthorProfile;
import id.radityo.wallpapy.Fragments.Featured.Model.Featured;
import id.radityo.wallpapy.Fragments.Featured.Model.Links;
import id.radityo.wallpapy.Fragments.Featured.Model.Urls;
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
import static id.radityo.wallpapy.Utils.Cons.LATEST_NEW;
import static id.radityo.wallpapy.Utils.Cons.OLDEST_NEW;
import static id.radityo.wallpapy.Utils.Cons.POPULAR_NEW;

public class FragmentFeatured extends Fragment {

    SwipeRefreshLayout mSwipeRefresh;
    RecyclerView mRecyclerView;
    LinearLayout mLinearLayout;
    ProgressBar mProgressBar;

    List<Featured> mFeaturedList = new ArrayList<>();
    FeaturedAdapter mFeaturedAdapter;
    EndlessOnScrollListener mEndlessScrollListener = null;

    private String mSortBy = LATEST_NEW;
    private MainActivity mActivity;

    private void initViewById(View view) {
        mSwipeRefresh = view.findViewById(R.id.refresh_random);
        mRecyclerView = view.findViewById(R.id.recycler_random);
        mLinearLayout = view.findViewById(R.id.offline_fragment_featured);
        mProgressBar = view.findViewById(R.id.progress_random);

        mLinearLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(false);
        mFeaturedAdapter = new FeaturedAdapter(mActivity, mFeaturedList);
        mRecyclerView.setAdapter(mFeaturedAdapter);

        mEndlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                requestCuratedPhotos(CLIENT_ID, page, mSortBy);
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
        View view = inflater.inflate(R.layout.fragment_featured, container, false);

        initViewById(view);

        initRecyclerView();

        requestCuratedPhotos(CLIENT_ID, 1, mSortBy);

        pullToRefresh(CLIENT_ID, mSortBy);

        return view;
    }

    private void requestCuratedPhotos(final String clientId, final int page, final String orderBy) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.getCuratedPhotos(clientId, page, 20, orderBy);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        mLinearLayout.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mSwipeRefresh.setRefreshing(false);

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

                            mFeaturedList.add(featured);
                        }

                        mFeaturedAdapter.notifyDataSetChanged();

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

                    Log.e(TAG, "onResponseNotSuccessful: FragmentFeatured");
                    Toast.makeText(mActivity,
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

    private void pullToRefresh(final String clientId, final String orderBy) {
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mProgressBar.setVisibility(View.GONE);
                mLinearLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);

                mEndlessScrollListener.resetState();
                mFeaturedList.clear();
                mFeaturedAdapter.notifyDataSetChanged();

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
                Intent search = new Intent(mActivity, SearchActivity.class);
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
        mSortBy = sortBy;
        Log.e(TAG, "mSortBy: " + mSortBy);
        Log.e(TAG, "SORTBY: " + sortBy);

        GridLayoutManager glm = (GridLayoutManager) mRecyclerView.getLayoutManager();
        glm.scrollToPositionWithOffset(0, 0);

        mEndlessScrollListener.resetState();
        mFeaturedList.clear();
        mFeaturedAdapter.notifyDataSetChanged();

        mRecyclerView.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        requestCuratedPhotos(CLIENT_ID, 1, mSortBy);
        pullToRefresh(CLIENT_ID, mSortBy);
    }
}
