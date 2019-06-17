package id.radityo.wallpapy.Activities.DetailAuthor;

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
import android.widget.TextView;
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

import static id.radityo.wallpapy.Fragments.New.FragmentNew.TAG;
import static id.radityo.wallpapy.Utils.Cons.CLIENT_ID;

public class TabUserLikes extends Fragment {
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private LinearLayout mLayoutNetwork;
    private ProgressBar mProgressBar;
    private TextView mTvEmpty;

    private EndlessOnScrollListener mEndlessScrollListener;
    private DetailAuthorActivity mActivity;
    private List<New> mNewList = new ArrayList<>();
    private NewAdapter mNewAdapter;

    private String mUsername;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (DetailAuthorActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_photos, container, false);

        mUsername = mActivity.getUsername();
        Log.e(TAG, "username ocv: " + mUsername);

        initView(view);

        initRecyclerView(container);

        requestUserLikes(container, CLIENT_ID, mUsername, 1);

        pullToRefresh(container, CLIENT_ID, mUsername);

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

    private void initRecyclerView(final ViewGroup container) {
        mRecyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(false);

        mNewAdapter = new NewAdapter(getActivity(), mNewList);
        mRecyclerView.setAdapter(mNewAdapter);
        mEndlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                requestUserLikes(container, CLIENT_ID, mUsername, page);
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }

    private void requestUserLikes(
            final ViewGroup container,
            final String clientId,
            final String username,
            final int page) {

        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.getUserLikes(username, clientId, 20, page);
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

                            JSONObject objRoot = array.getJSONObject(i);

                            String id = objRoot.getString("id");
                            String createdAt = objRoot.getString("created_at");
                            String updatedAt = objRoot.getString("updated_at");
                            int width = objRoot.getInt("width");
                            int height = objRoot.getInt("height");
                            String color = objRoot.getString("color");
                            String description = objRoot.getString("description");
                            String alternateDescription = objRoot.getString("alt_description");

                            // URLS
                            JSONObject objUrls = objRoot.getJSONObject("urls");
                            String regular = objUrls.getString("regular");

                            // AUTHOR
                            JSONObject objAuthor = objRoot.getJSONObject("user");
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
                            mTvEmpty.setText(getString(R.string.likes_empty));
                            mTvEmpty.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e(TAG, "onResponseNotSuccessful: Tab Photos");

                    Toast.makeText(mActivity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    mRecyclerView.setVisibility(View.GONE);
                    mLayoutNetwork.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefresh.setRefreshing(false);

                    pullToRefresh(container, clientId, username);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Tab Photos");
                t.printStackTrace();

                Toast.makeText(mActivity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mLayoutNetwork.setVisibility(View.VISIBLE);
                mSwipeRefresh.setRefreshing(false);

                pullToRefresh(container, clientId, username);
            }
        });
    }

    private void pullToRefresh(
            final ViewGroup container,
            final String clientId,
            final String username) {

        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEndlessScrollListener.resetState();
                mNewList.clear();
                mNewAdapter.notifyDataSetChanged();

                requestUserLikes(container, clientId, username, 1);
            }
        });
    }
}
