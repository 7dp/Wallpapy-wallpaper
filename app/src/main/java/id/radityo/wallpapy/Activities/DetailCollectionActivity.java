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
import id.radityo.wallpapy.Fragments.New.Model.Author.Author;
import id.radityo.wallpapy.Fragments.New.Model.Author.AuthorLinks;
import id.radityo.wallpapy.Fragments.New.Model.Author.AuthorProfile;
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

import static id.radityo.wallpapy.Utils.Cons.CLIENT_ID;

public class DetailCollectionActivity extends AppCompatActivity {

    public static final String TAG = "wallpapy";
    private int mCollectionId;
    private String mAuthorMedium;
    private String mAuthorName;
    private String mCollectionTitle;
    private String mUserId;
    private String mUsername;
    private String mProfileSmall;
    private String mProfileLarge;
    private String mUserLocation;
    private String mBio;
    private String mDescription;

    Toolbar mToolbar;
    ImageView mIvAuthor;
    TextView mTvAuthor, mTvCollectionName;
    SwipeRefreshLayout mSwipeRefresh;
    LinearLayout mLinearNetwork, mContainerAuthor;
    ProgressBar mProgressBar;
    RecyclerView mRecyclerView;

    List<New> mNewList = new ArrayList<>();
    NewAdapter mNewAdapter;
    EndlessOnScrollListener mEndlessScrollListener = null;

    private void initView() {
        mToolbar = findViewById(R.id.toolbar_detail_collection);
        mRecyclerView = findViewById(R.id.recycler_detail_collections);
        mSwipeRefresh = findViewById(R.id.refresh_detail_collection);
        mLinearNetwork = findViewById(R.id.offline_detail_collection);
        mContainerAuthor = findViewById(R.id.container_author_collection_detail);
        mProgressBar = findViewById(R.id.progress_detail_collection);
        mIvAuthor = findViewById(R.id.iv_author_collection);
        mTvAuthor = findViewById(R.id.tv_author_detail_collection);
        mTvCollectionName = findViewById(R.id.tv_collection_detail_name);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar t = getSupportActionBar();
        t.setDisplayShowTitleEnabled(false);
        t.setDisplayShowHomeEnabled(true);
        t.setDisplayHomeAsUpEnabled(true);
        t.setHomeAsUpIndicator(R.drawable.ic_back_black_24);
    }

    private void setDefaultProperties() {
        mLinearNetwork.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        mTvAuthor.setSelected(true);
        mTvCollectionName.setSelected(true);
        mTvAuthor.setText(mAuthorName);
        mTvCollectionName.setText(mCollectionTitle);

        Glide.with(this)
                .load(mAuthorMedium)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .fallback(new ColorDrawable(Color.GRAY))
                .error(new ColorDrawable(Color.WHITE))
                .circleCrop()
                .into(mIvAuthor);

        mContainerAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCollectionActivity.this, DetailAuthorActivity.class);
                intent.putExtra("user_id", mUserId);
                intent.putExtra("name", mAuthorName);
                intent.putExtra("user_name", mUsername);
                intent.putExtra("profile_image_small", mProfileSmall);
                intent.putExtra("profile_image_large", mProfileLarge);
                intent.putExtra("location", mUserLocation);
                intent.putExtra("author_med", mAuthorMedium);
                intent.putExtra("bio", mBio);
                startActivity(intent);
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(DetailCollectionActivity.this, 2));
        mRecyclerView.setMotionEventSplittingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mNewAdapter = new NewAdapter(this, mNewList);
        mRecyclerView.setAdapter(mNewAdapter);

        mEndlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                requestCollectionPhotos(mCollectionId, page, CLIENT_ID);
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }

    private void obtainDataFromIntent() {
        Intent i = getIntent();
        mCollectionId = i.getIntExtra("collection_id", -1);
        mAuthorName = i.getStringExtra("author_name");
        mAuthorMedium = i.getStringExtra("author_medium");
        mCollectionTitle = i.getStringExtra("title");
        mUserId = i.getStringExtra("user_id");
        mUsername = i.getStringExtra("user_name");
        mProfileSmall = i.getStringExtra("profile_image_small");
        mProfileLarge = i.getStringExtra("profile_image_large");
        mUserLocation = i.getStringExtra("location");
        mBio = i.getStringExtra("bio");
        mDescription = i.getStringExtra("desc");
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

        obtainDataFromIntent();

        Log.e(TAG, "mCollectionId: " + mCollectionId);
        Log.e(TAG, "collectionTitle: " + mCollectionTitle);
        Log.e(TAG, "mAuthorName: " + mAuthorName);
        Log.e(TAG, "mAuthorMedium: " + mAuthorMedium);
        Log.e(TAG, "mCollectionTitle: " + mCollectionTitle);
        Log.e(TAG, "mUserId: " + mUserId);
        Log.e(TAG, "mUsername: " + mUsername);
        Log.e(TAG, "profile_medium: " + mProfileSmall);
        Log.e(TAG, "mProfileLarge: " + mProfileLarge);
        Log.e(TAG, "mUserLocation: " + mUserLocation);
        Log.e(TAG, "mBio: " + mBio);

        initView();

        initToolbar();

        setDefaultProperties();

        initRecyclerView();

        requestCollectionPhotos(mCollectionId, 1, CLIENT_ID);

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

                        mSwipeRefresh.setRefreshing(false);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mLinearNetwork.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.GONE);

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

                            // USER
                            JSONObject userObj = rootObject.getJSONObject("user");
                            String name = userObj.getString("name");

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

                            mNewList.add(anew);
                        }

                        mNewAdapter.notifyDataSetChanged();

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

                    mRecyclerView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mLinearNetwork.setVisibility(View.GONE);
                    mSwipeRefresh.setRefreshing(false);

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

                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mLinearNetwork.setVisibility(View.VISIBLE);
                mSwipeRefresh.setRefreshing(false);

                pullToRefresh();
            }
        });
    }

    private void pullToRefresh() {
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mProgressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mLinearNetwork.setVisibility(View.GONE);

                mEndlessScrollListener.resetState();
                mNewList.clear();
                mNewAdapter.notifyDataSetChanged();

                requestCollectionPhotos(mCollectionId, 1, CLIENT_ID);
            }
        });
    }
}
