package id.radityo.wallpapy.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.radityo.wallpapy.Activities.DetailAuthor.DetailAuthorActivity;
import id.radityo.wallpapy.MyFragment.New.Model.Author.Author;
import id.radityo.wallpapy.MyFragment.New.Model.Author.AuthorLinks;
import id.radityo.wallpapy.MyFragment.New.Model.Author.AuthorProfile;
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

import static id.radityo.wallpapy.Constants.CLIENT_ID;

public class DetailCollectionActivity extends AppCompatActivity {

    public static final String TAG = "wallpapy";
    private int collectionId;
    private String authorMed;
    private String authorName;
    private String collTitle;
    private String user_id;
    private String username;
    private String profile_small;
    private String profile_large;
    private String user_location;
    private String bio;
    private String description;

    Toolbar toolbar;
    ImageView ivAuthor;
    TextView tvAuthor, tvCollectionName;
    SwipeRefreshLayout swipeRefresh;
    LinearLayout linearOffline, containerAuthor;
    ProgressBar progressBar;
    RecyclerView recyclerView;

    List<New> newList = new ArrayList<>();
    NewAdapter newAdapter;
    EndlessOnScrollListener endlessScrollListener;

    private void findViewById() {
        toolbar = findViewById(R.id.toolbar_detail_collection);
        recyclerView = findViewById(R.id.recycler_detail_collections);
        swipeRefresh = findViewById(R.id.refresh_detail_collection);
        linearOffline = findViewById(R.id.linear_internet_detail_collection);
        containerAuthor = findViewById(R.id.container_author_collection_detail);
        progressBar = findViewById(R.id.progress_detail_collection);
        ivAuthor = findViewById(R.id.iv_author_collection);
        tvAuthor = findViewById(R.id.tv_author_detail_collection);
        tvCollectionName = findViewById(R.id.tv_collection_detail_name);
    }

    private void setView() {
        setSupportActionBar(toolbar);
        ActionBar t = getSupportActionBar();
        t.setDisplayShowTitleEnabled(false);
        t.setDisplayShowHomeEnabled(true);
        t.setDisplayHomeAsUpEnabled(true);
        t.setHomeAsUpIndicator(R.drawable.ic_back_black_24);

        linearOffline.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        tvAuthor.setText(authorName);
        tvCollectionName.setText(collTitle);
        tvAuthor.setSelected(true);
        tvCollectionName.setSelected(true);

        Glide.with(this)
                .load(authorMed)
                .transition(DrawableTransitionOptions.withCrossFade())
                .fallback(new ColorDrawable(Color.GRAY))
                .error(new ColorDrawable(Color.WHITE))
                .circleCrop()
                .into(ivAuthor);

        containerAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailCollectionActivity.this, DetailAuthorActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("name", authorName);
                intent.putExtra("user_name", username);
                intent.putExtra("profile_image_small", profile_small);
                intent.putExtra("profile_image_large", profile_large);
                intent.putExtra("location", user_location);
                intent.putExtra("author_med", authorMed);
                intent.putExtra("bio", bio);
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(DetailCollectionActivity.this, 2));
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setHasFixedSize(true);
        newAdapter = new NewAdapter(DetailCollectionActivity.this, newList);
        recyclerView.setAdapter(newAdapter);

        endlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                Log.e(TAG, "onLoadMore: page -> " + page);
                requestCollectionPhotos(collectionId, page, CLIENT_ID);
            }
        };

        recyclerView.addOnScrollListener(endlessScrollListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_collection);

        Intent i = getIntent();
        collectionId = i.getIntExtra("collection_id", -1);
        authorName = i.getStringExtra("author_name");
        authorMed = i.getStringExtra("author_medium");
        collTitle = i.getStringExtra("title");
        user_id = i.getStringExtra("user_id");
        username = i.getStringExtra("user_name");
        profile_small = i.getStringExtra("profile_image_small");
        profile_large = i.getStringExtra("profile_image_large");
        user_location = i.getStringExtra("location");
        bio = i.getStringExtra("bio");
        description = i.getStringExtra("desc");

        Log.e(TAG, "collectionId: " + collectionId);
        Log.e(TAG, "collectionTitle: " + collTitle);
        Log.e(TAG, "authorName: " + authorName);
        Log.e(TAG, "authorMed: " + authorMed);
        Log.e(TAG, "collTitle: " + collTitle);
        Log.e(TAG, "user_id: " + user_id);
        Log.e(TAG, "username: " + username);
        Log.e(TAG, "profile_medium: " + profile_small);
        Log.e(TAG, "profile_large: " + profile_large);
        Log.e(TAG, "user_location: " + user_location);
        Log.e(TAG, "bio: " + bio);

        findViewById();

        setView();

        initRecyclerView();

        requestCollectionPhotos(collectionId, 1, CLIENT_ID);

        pullToRefresh();
    }

    private void requestCollectionPhotos(final long collectionId, final int page, final String clientId) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.getCollectionPhotos(collectionId, clientId, 20, page);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        swipeRefresh.setRefreshing(false);
                        recyclerView.setVisibility(View.VISIBLE);
                        linearOffline.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);

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

                            // URLS
                            JSONObject urlsObj = rootObject.getJSONObject("urls");
                            String regular = urlsObj.getString("regular");

                            // LINKS
                            JSONObject linksObj = rootObject.getJSONObject("links");

                            // USER
                            JSONObject userObj = rootObject.getJSONObject("user");
                            String name = userObj.getString("name");
//                            String instagram = userObj.getString("instagram_username");
//                            int total_collections = userObj.getInt("total_collections");
//                            int total_likes = userObj.getInt("total_likes");
//                            int total_photos = userObj.getInt("total_photos");
//                            int accepted_tos = userObj.getInt("accepted_tos");


                            // user links
                            JSONObject userLinksObj = userObj.getJSONObject("links");
                            String user_self = userLinksObj.getString("self");
                            String user_html = userLinksObj.getString("html");
                            String user_photos = userLinksObj.getString("photos");
                            String user_likes = userLinksObj.getString("likes");
                            String user_portfolio = userLinksObj.getString("portfolio");
                            String user_following = userLinksObj.getString("following");
                            String user_followers = userLinksObj.getString("followers");

                            // user profile
                            JSONObject profileImage = userObj.getJSONObject("profile_image");
                            String profile_small = profileImage.getString("small");

                            Urls urls = new Urls();
                            urls.setRegular(regular);

                            AuthorLinks authorLinks = new AuthorLinks();
                            authorLinks.setSelf(user_self);
                            authorLinks.setHtml(user_html);
                            authorLinks.setPhotos(user_photos);
                            authorLinks.setLikes(user_likes);
                            authorLinks.setPortfolio(user_portfolio);
                            authorLinks.setFollowing(user_following);
                            authorLinks.setFollowers(user_followers);

                            AuthorProfile authorProfile = new AuthorProfile();
                            authorProfile.setSmall(profile_small);

                            Author author = new Author();
                            author.setName(name);
                            author.setLinks(authorLinks);
                            author.setAuthorProfile(authorProfile);

                            New anew = new New();
                            anew.setId(id);
                            anew.setCreatedAt(createdAt);
                            anew.setUpdatedAt(updatedAt);
                            anew.setWidth(width);
                            anew.setHeight(height);
                            anew.setColor(color);
                            anew.setDescription(description);
                            anew.setAltDescription(alternateDescription);

                            anew.setAuthor(author);
                            anew.setUrls(urls);

                            newList.add(anew);
                        }

                        newAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    Log.e(TAG, "onResponseNotSuccessful: DetailCollectionActivity");
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    linearOffline.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);

                    pullToRefresh();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: DetailCollectionActivity");
                t.printStackTrace();

                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.server_error),
                        Toast.LENGTH_SHORT)
                        .show();

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                linearOffline.setVisibility(View.VISIBLE);
                swipeRefresh.setRefreshing(false);

                pullToRefresh();
            }
        });
    }

    private void pullToRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                linearOffline.setVisibility(View.GONE);

                endlessScrollListener.resetState();
                newList.clear();
                newAdapter.notifyDataSetChanged();

                requestCollectionPhotos(collectionId, 1, CLIENT_ID);
            }
        });
    }
}
