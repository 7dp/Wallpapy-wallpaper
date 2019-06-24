package id.radityo.wallpapy.Fragments.New;

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
import id.radityo.wallpapy.Fragments.New.Model.Author.Author;
import id.radityo.wallpapy.Fragments.New.Model.New;
import id.radityo.wallpapy.Fragments.New.Model.Urls;
import id.radityo.wallpapy.R;
import id.radityo.wallpapy.Request.APIService;
import id.radityo.wallpapy.Request.ApiClient;
import id.radityo.wallpapy.Utils.EndlessOnScrollListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.radityo.wallpapy.Utils.Cons.BROADCAST_NEW;
import static id.radityo.wallpapy.Utils.Cons.CLIENT_ID;
import static id.radityo.wallpapy.Utils.Cons.LATEST_NEW;
import static id.radityo.wallpapy.Utils.Cons.OLDEST_NEW;
import static id.radityo.wallpapy.Utils.Cons.POPULAR_NEW;


public class FragmentNew extends Fragment {
    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeRefresh;
    LinearLayout mLinearLayout;

    NewAdapter mNewAdapter;
    List<New> mNewList = new ArrayList<>();
    EndlessOnScrollListener mEndlessScrollListener = null;

    MainActivity mActivity;
    private String mSortBy = LATEST_NEW;
    public static final String TAG = "wallpapy";

    private void initViewById(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_latest);
        mProgressBar = view.findViewById(R.id.progress_new);
        mSwipeRefresh = view.findViewById(R.id.refresh_latest);
        mLinearLayout = view.findViewById(R.id.offline_fragment_new);

        mRecyclerView.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mRecyclerView.setMotionEventSplittingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mNewAdapter = new NewAdapter(getActivity(), mNewList);
        mRecyclerView.setAdapter(mNewAdapter);

        mEndlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                requestAllPhotos(CLIENT_ID, page, mSortBy);
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

        View view = inflater.inflate(R.layout.fragment_new, container, false);

        initViewById(view);

        initRecyclerView();

        requestAllPhotos(CLIENT_ID, 1, mSortBy);

        pullToRefresh(CLIENT_ID, mSortBy);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.registerReceiver(mBroadcastReceiver, new IntentFilter(BROADCAST_NEW));
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(BROADCAST_NEW)) {
                GridLayoutManager glm = (GridLayoutManager) mRecyclerView.getLayoutManager();
                glm.scrollToPositionWithOffset(0, 0);
                mRecyclerView.stopScroll();
            }
        }
    };

    private void requestAllPhotos(final String clientId, final int page, final String orderBy) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.getAllPhotos(clientId, 20, page, orderBy);
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

                            JSONObject objLatest = array.getJSONObject(i);

                            String id = objLatest.getString("id");
                            String createdAt = objLatest.getString("created_at");
                            String updatedAt = objLatest.getString("updated_at");
                            int width = objLatest.getInt("width");
                            int height = objLatest.getInt("height");
                            String color = objLatest.getString("color");
                            String description = objLatest.getString("description");
                            String alternateDescription = objLatest.getString("alt_description");

                            JSONObject objUrls = objLatest.getJSONObject("urls");
                            String regular = objUrls.getString("regular");

                            JSONObject objAuthor = objLatest.getJSONObject("user");
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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    Log.e(TAG, "onResponseNotSuccessful: FragmentNew");
                    Toast.makeText(mActivity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    mRecyclerView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mLinearLayout.setVisibility(View.GONE);
                    mSwipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, orderBy);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: FragmentNew");
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
                mNewList.clear();
                mNewAdapter.notifyDataSetChanged();

                requestAllPhotos(clientId, 1, orderBy);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_new_featured:
                Intent search = new Intent(mActivity, SearchActivity.class);
                startActivity(search);
                break;
            case R.id.action_sort_latest:
                sortPhotosBy(LATEST_NEW);
                break;
            case R.id.action_sort_oldest:
                sortPhotosBy(OLDEST_NEW);
                break;
            case R.id.action_sort_popular:
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
        mNewList.clear();
        mNewAdapter.notifyDataSetChanged();

        mRecyclerView.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        requestAllPhotos(CLIENT_ID, 1, mSortBy);
        pullToRefresh(CLIENT_ID, mSortBy);
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.unregisterReceiver(mBroadcastReceiver);
    }
}
