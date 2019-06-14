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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.radityo.wallpapy.MyFragment.New.Model.Author.Author;
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
import static id.radityo.wallpapy.MyFragment.New.FragmentNew.TAG;

public class TabPhotos extends Fragment {
    RecyclerView recyclerView;
    LinearLayout layoutOffline;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar progressBar;
    TextView tvEmpty;

    List<New> newList = new ArrayList<>();
    NewAdapter newAdapter;
    EndlessOnScrollListener endlessScrollListener;
    DetailAuthorActivity activity;
    String username;

    private void findViewById(View view) {
        recyclerView = view.findViewById(R.id.recycler_tab_photos);
        layoutOffline = view.findViewById(R.id.linear_internet_tab_photos);
        swipeRefresh = view.findViewById(R.id.refresh_tab_photos);
        progressBar = view.findViewById(R.id.progress_tab_photos);
        tvEmpty = view.findViewById(R.id.tv_empty_tab_photos);

        layoutOffline.setVisibility(View.GONE);
    }

    private void initRecyclerView(final ViewGroup container) {
        recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setMotionEventSplittingEnabled(false);

        newAdapter = new NewAdapter(getActivity(), newList);
        recyclerView.setAdapter(newAdapter);

        endlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                requestUserPhotos(CLIENT_ID, username, page, container);
            }
        };

        recyclerView.addOnScrollListener(endlessScrollListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DetailAuthorActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_photos, container, false);

        findViewById(view);

        initRecyclerView(container);

        username = ((DetailAuthorActivity) getActivity()).getUsername();
        Log.e(TAG, "username ocv: " + username);

        requestUserPhotos(CLIENT_ID, username, 1, container);

        pullToRefresh(CLIENT_ID, username, container);

        return view;
    }

    private void requestUserPhotos(
            final String clientId,
            final String username,
            final int page,
            final ViewGroup container) {

        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.getUserPhotos(username, clientId, 20, page);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        tvEmpty.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        layoutOffline.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        swipeRefresh.setRefreshing(false);

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

                            newList.add(unew);
                        }

                        newAdapter.notifyDataSetChanged();

                        if (newList.isEmpty()) {
                            Log.e(TAG, "EMPTY: " + newList.size());
                            tvEmpty.setText(getString(R.string.photos_empty));
                            tvEmpty.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    Log.e(TAG, "onResponseNotSuccessful: Tab Photos");
                    Toast.makeText(activity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    recyclerView.setVisibility(View.GONE);
                    layoutOffline.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, username, container);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Tab Photos");
                t.printStackTrace();

                Toast.makeText(activity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                layoutOffline.setVisibility(View.VISIBLE);
                swipeRefresh.setRefreshing(false);

                pullToRefresh(clientId, username, container);
            }
        });
    }

    private void pullToRefresh(
            final String clientId,
            final String username,
            final ViewGroup container) {
        swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                endlessScrollListener.resetState();
                newList.clear();
                newAdapter.notifyDataSetChanged();

                requestUserPhotos(clientId, username, 1, container);
            }
        });
    }
}
