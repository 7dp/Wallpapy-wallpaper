package id.radityo.wallpapy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.radityo.wallpapy.Activities.DetailActivity;
import id.radityo.wallpapy.Activities.MainActivity;
import id.radityo.wallpapy.Activities.Search.SearchActivity;
import id.radityo.wallpapy.MyFragment.New.Model.Author.Author;
import id.radityo.wallpapy.MyFragment.New.Model.Author.AuthorProfile;
import id.radityo.wallpapy.MyFragment.New.Model.New;
import id.radityo.wallpapy.MyFragment.New.Model.Urls;
import id.radityo.wallpapy.MyFragment.New.NewAdapter;
import id.radityo.wallpapy.Request.APIService;
import id.radityo.wallpapy.Request.ApiClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static id.radityo.wallpapy.Constants.CLIENT_ID;
import static id.radityo.wallpapy.Constants.LATEST_NEW;
import static id.radityo.wallpapy.Constants.OLDEST_NEW;
import static id.radityo.wallpapy.Constants.POPULAR_NEW;
import static id.radityo.wallpapy.MyFragment.New.FragmentNew.TAG;

public class BackupFragmentNew extends Fragment implements ViewPagerEx.OnPageChangeListener {

    private RecyclerView recyclerView;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout linearLayoutNetwork;
    private Toolbar toolbar;
    private SliderLayout sliderLayout;
    private MainActivity activity;

    private NewAdapter newAdapter;
    private List<New> newList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<String> colorList = new ArrayList<>();
    private List<String> idList = new ArrayList<>();
    private List<String> urlList = new ArrayList<>();

    private boolean isLoadData = true;
    private int currentPage = 1;
    private static String sortby;

    private void initViewById(View view) {
        recyclerView = view.findViewById(R.id.recycler_latest_backup);
        progressBar = view.findViewById(R.id.progress_new_backup);
        swipeRefresh = view.findViewById(R.id.swipe_refresh_backup);
        linearLayoutNetwork = view.findViewById(R.id.linear_internet_latest_backup);
        toolbar = view.findViewById(R.id.toolbar_fragment_new_backup);
        sliderLayout = view.findViewById(R.id.slider_latest_backup);
        nestedScrollView = view.findViewById(R.id.nestedscrollview_backup);
    }

    private void setDefaultVisibility() {
        nestedScrollView.setVisibility(View.GONE);
        linearLayoutNetwork.setVisibility(View.GONE);
    }

    private void initToolbar() {
        activity.setSupportActionBar(toolbar);
        ActionBar ab = activity.getSupportActionBar();
        ab.setElevation(6f);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setHasFixedSize(true);
        newAdapter = new NewAdapter(getActivity(), newList);
        recyclerView.setAdapter(newAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.backup_fragment_new, container);

        initViewById(view);
        initToolbar();
        initRecyclerView();
        setDefaultVisibility();

        // hit here
        requestSliderPhotos(CLIENT_ID, LATEST_NEW);
        requestAllPhotos(CLIENT_ID, 1, LATEST_NEW);
        bottomScrollNSV(LATEST_NEW);

        return view;
    }

    private void requestAllPhotos(final String clientId, final int page, final String orderBy) {

        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.getAllPhotos(clientId, 20, page, orderBy);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        linearLayoutNetwork.setVisibility(View.GONE);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);

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

                            newList.add(unew);
                        }

                        newAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    Log.e(TAG, "onResponseNotSuccessful: Backup");
                    Toast.makeText(activity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    nestedScrollView.setVisibility(View.GONE);
                    linearLayoutNetwork.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, orderBy);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: Backup");
                t.printStackTrace();

                nestedScrollView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                linearLayoutNetwork.setVisibility(View.VISIBLE);

                Toast.makeText(activity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                pullToRefresh(clientId, orderBy);
            }
        });
    }

    private void requestSliderPhotos(final String clientId, final String orderBy) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.getCuratedPhotos(clientId, 1, 6, POPULAR_NEW);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    try {
                        swipeRefresh.setRefreshing(false);
                        nameList.clear();
                        colorList.clear();
                        idList.clear();
                        urlList.clear();

                        JSONArray array = new JSONArray(response.body().string());

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject objPopular = array.getJSONObject(i);

                            String id = objPopular.getString("id");
                            String createdAt = objPopular.getString("created_at");
                            String updatedAt = objPopular.getString("updated_at");
                            int width = objPopular.getInt("width");
                            int height = objPopular.getInt("height");
                            String color = objPopular.getString("color");
                            String description = objPopular.getString("description");
                            String alternateDescription = objPopular.getString("alt_description");

                            JSONObject objUrls = objPopular.getJSONObject("urls");
                            String regular = objUrls.getString("regular");

                            // AUTHOR
                            JSONObject objAuthor = objPopular.getJSONObject("user");
                            String name = objAuthor.getString("name");

                            JSONObject profileImage = objAuthor.getJSONObject("profile_image");
                            String profile_small = profileImage.getString("small");

                            Urls urls = new Urls();
                            urls.setRegular(regular);

                            AuthorProfile authorProfile = new AuthorProfile();
                            authorProfile.setSmall(profile_small);

                            Author author = new Author();
                            author.setName(name);
                            author.setAuthorProfile(authorProfile);

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

                            Log.e(TAG, "### regular: " + regular);

                            urlList.add(regular);
                            colorList.add(color);
                            nameList.add(name);
                            idList.add(id);
                        }

                        Log.e(TAG, "urlList size: " + urlList.size());
                        Log.e(TAG, "idList size: " + idList.size());
                        Log.e(TAG, "nameList size: " + nameList.size());
                        Log.e(TAG, "colorList size: " + colorList.size());

                        for (int i = 0; i < idList.size(); i++) {

                            TextSliderView sliderView = new TextSliderView(activity);

                            final int finalI = i;
                            sliderView
                                    .image(urlList.get(i))
                                    .description(nameList.get(i))
                                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                    .setOnSliderClickListener(
                                            new BaseSliderView.OnSliderClickListener() {
                                                @Override
                                                public void onSliderClick(BaseSliderView slider) {
                                                    sliderLayout.stopAutoCycle();

                                                    Intent intent = new Intent(activity, DetailActivity.class);
                                                    intent.putExtra("url_regular", urlList.get(finalI));
                                                    intent.putExtra("id", idList.get(finalI));
                                                    intent.putExtra("color", colorList.get(finalI));
                                                    startActivity(intent);
                                                }
                                            });

                            sliderView.bundle(new Bundle());
                            sliderLayout.addSlider(sliderView);
                        }

                        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
                        sliderLayout.setCustomAnimation(new DescriptionAnimation());
                        sliderLayout.addOnPageChangeListener(BackupFragmentNew.this);

                        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
                        sliderLayout.getPagerIndicator().setDefaultSelectedIndicatorSize(8, 8, PagerIndicator.Unit.DP);
                        sliderLayout.getPagerIndicator().setDefaultUnselectedIndicatorSize(5, 5, PagerIndicator.Unit.DP);
                        sliderLayout.getPagerIndicator().setDefaultIndicatorColor(Color.WHITE, R.color.white30);
                        sliderLayout.setCurrentPosition(0);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sliderLayout.startAutoCycle();
                            }
                        }, 500L);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    Log.e(TAG, "onResponseNotSuccessful: Backup");
                    Toast.makeText(activity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    Log.e(TAG, "not success " + response.message());
                    swipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, orderBy);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: FragmentNew");
                t.printStackTrace();

                nestedScrollView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                linearLayoutNetwork.setVisibility(View.VISIBLE);
                swipeRefresh.setRefreshing(false);

                Toast.makeText(activity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                pullToRefresh(clientId, orderBy);
            }
        });
    }

    private void pullToRefresh(final String clientId, final String orderBy) {

        swipeRefresh.setColorSchemeResources(R.color.colorPrimaryDark);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.GONE);
                linearLayoutNetwork.setVisibility(View.GONE);

                newList.clear();
                newAdapter.notifyDataSetChanged();
                nestedScrollView.setVisibility(View.GONE);

                nameList.clear();
                colorList.clear();
                idList.clear();
                urlList.clear();

                requestAllPhotos(clientId, 1, orderBy);
                requestSliderPhotos(clientId, orderBy);
            }
        });
    }

    private void bottomScrollNSV(final String orderBy) {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView view, int sx, int sy, int osx, int osy) {
                if (view.getChildAt(view.getChildCount() - 1) != null) {

                    if ((sy >= (view.getChildAt(view.getChildCount() - 1).getMeasuredHeight() - view.getMeasuredHeight()))
                            && sy > osy) {

                        int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                        int pastVisibleItems = manager.findFirstVisibleItemPosition();
                        if (isLoadData) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                // load data
                                currentPage++;
                                requestAllPhotos(CLIENT_ID, currentPage, orderBy);
                            }
                        }
                    }
                }
            }
        });
    }

    private void addSearchAction(MenuItem searchItem) {
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent search = new Intent(activity, SearchActivity.class);
                startActivity(search);
                return true;
            }
        });
    }

    private void sortPhotosBy(MenuItem sortItem, final String sortBy) {
        sortItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sortby = sortBy;
                Log.e(TAG, "sortby: " + sortby);
                Log.e(TAG, "SORTBY: " + sortBy);

                newList.clear();
                newAdapter.notifyDataSetChanged();
                nestedScrollView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                requestAllPhotos(CLIENT_ID, 1, sortby);
                pullToRefresh(CLIENT_ID, sortby);
                return true;
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPagerEx.SCROLL_STATE_IDLE:
            case ViewPagerEx.SCROLL_STATE_SETTLING:
                break;
            case ViewPagerEx.SCROLL_STATE_DRAGGING:
                sliderLayout.stopAutoCycle();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderLayout.stopAutoCycle();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.new_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search_new_featured);
        addSearchAction(searchItem);

        MenuItem sortLatest = menu.findItem(R.id.action_sort_latest);
        MenuItem sortOldest = menu.findItem(R.id.action_sort_oldest);
        MenuItem sortPopular = menu.findItem(R.id.action_sort_popular);

        sortPhotosBy(sortLatest, LATEST_NEW);
        sortPhotosBy(sortOldest, OLDEST_NEW);
        sortPhotosBy(sortPopular, POPULAR_NEW);
    }
}
