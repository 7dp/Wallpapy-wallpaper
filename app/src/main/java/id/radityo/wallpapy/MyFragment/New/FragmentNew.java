package id.radityo.wallpapy.MyFragment.New;

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
import id.radityo.wallpapy.MyFragment.New.Model.Author.Author;
import id.radityo.wallpapy.MyFragment.New.Model.New;
import id.radityo.wallpapy.MyFragment.New.Model.Urls;
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


public class FragmentNew extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefresh;
    LinearLayout linearLayout;

    NewAdapter newAdapter;
    List<New> newList = new ArrayList<>();
    EndlessOnScrollListener endlessScrollListener = null;

    MainActivity activity;
    private String sortby = LATEST_NEW;
    public static final String TAG = "wallpapy";

    private void initViewById(View view) {
        recyclerView = view.findViewById(R.id.recycler_latest);
        progressBar = view.findViewById(R.id.progress_new);
        swipeRefresh = view.findViewById(R.id.refresh_latest);
        linearLayout = view.findViewById(R.id.linear_internet_latest);

        recyclerView.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
    }


    private void initRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setMotionEventSplittingEnabled(false);
        recyclerView.setHasFixedSize(true);
        newAdapter = new NewAdapter(getActivity(), newList);
        recyclerView.setAdapter(newAdapter);

        endlessScrollListener = new EndlessOnScrollListener() {
            @Override
            public void onLoadMore(int page) {
                Log.e(TAG, "onLoadMore: page -> " + page);
                requestAllPhotos(CLIENT_ID, page, sortby);
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

        View view = inflater.inflate(R.layout.fragment_new, container, false);

        initViewById(view);

        initRecyclerView();

        requestAllPhotos(CLIENT_ID, 1, sortby);

        pullToRefresh(CLIENT_ID, sortby);

//        infiniteScroll(CLIENT_ID, LATEST_NEW);

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
                        linearLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
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

                    Log.e(TAG, "onResponseNotSuccessful: FragmentNew");
                    Toast.makeText(activity,
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);

                    pullToRefresh(clientId, orderBy);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: FragmentNew");
                t.printStackTrace();

                Toast.makeText(activity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                swipeRefresh.setRefreshing(false);

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
                linearLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);

                endlessScrollListener.resetState();
                newList.clear();
                newAdapter.notifyDataSetChanged();

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
                Intent search = new Intent(activity, SearchActivity.class);
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
        sortby = sortBy;
        Log.e(TAG, "sortby: " + sortby);
        Log.e(TAG, "SORTBY: " + sortBy);

        GridLayoutManager glm = (GridLayoutManager) recyclerView.getLayoutManager();
        glm.scrollToPositionWithOffset(0, 0);

        endlessScrollListener.resetState();
        newList.clear();
        newAdapter.notifyDataSetChanged();

        recyclerView.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        requestAllPhotos(CLIENT_ID, 1, sortby);
        pullToRefresh(CLIENT_ID, sortby);
    }


    //    private void requestCuratedPhotos(final String clientId) {
//        APIService service = ApiClient.getBaseUrl();
//        Call<ResponseBody> call = service.getCuratedPhotos(clientId, 1, 20, POPULAR_NEW);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//
//                if (response.isSuccessful()) {
//
//                    try {
////                        progressSlider.setVisibility(View.GONE);
//                        swipeRefresh.setRefreshing(false);
//
//                        nameList.clear();
//                        colorList.clear();
//                        idList.clear();
//                        urlList.clear();
//
//                        JSONArray array = new JSONArray(response.body().string());
//
//                        for (int i = 0; i < 6; i++) {
//
//                            JSONObject objPopular = array.getJSONObject(i);
//
//                            String id = objPopular.getString("id");
//                            String createdAt = objPopular.getString("created_at");
//                            String updatedAt = objPopular.getString("updated_at");
//                            int width = objPopular.getInt("width");
//                            int height = objPopular.getInt("height");
//                            String color = objPopular.getString("color");
//                            String description = objPopular.getString("description");
//                            String alternateDescription = objPopular.getString("alt_description");
//
//
//                            JSONObject objUrls = objPopular.getJSONObject("urls");
//                            String regular = objUrls.getString("regular");
//
//                            // AUTHOR
//                            JSONObject objAuthor = objPopular.getJSONObject("user");
//                            String name = objAuthor.getString("name");
//
//                            JSONObject profileImage = objAuthor.getJSONObject("profile_image");
//                            String profile_small = profileImage.getString("small");
//
//                            Urls urls = new Urls();
//                            urls.setRegular(regular);
//
//                            AuthorProfile authorProfile = new AuthorProfile();
//                            authorProfile.setSmall(profile_small);
//
//                            Author author = new Author();
//                            author.setName(name);
//                            author.setAuthorProfile(authorProfile);
//
//                            New unew = new New();
//                            unew.setId(id);
//                            unew.setCreatedAt(createdAt);
//                            unew.setUpdatedAt(updatedAt);
//                            unew.setWidth(width);
//                            unew.setHeight(height);
//                            unew.setColor(color);
//                            unew.setDescription(description);
//                            unew.setAltDescription(alternateDescription);
//
//                            unew.setUrls(urls);
//                            unew.setAuthor(author);
//
//                            Log.e(TAG, "### regular: " + regular);
//
//                            urlList.add(regular);
//                            colorList.add(color);
//                            nameList.add(name);
//                            idList.add(id);
//                        }
//
//                        Log.e(TAG, "urlList size: " + urlList.size());
//                        Log.e(TAG, "idList size: " + idList.size());
//                        Log.e(TAG, "nameList size: " + nameList.size());
//                        Log.e(TAG, "colorList size: " + colorList.size());
//
//                        for (int i = 0; i < idList.size(); i++) {
//
//                            TextSliderView sliderView = new TextSliderView(getActivity());
//
//                            final int finalI = i;
//                            sliderView
//                                    .image(urlList.get(i))
//                                    .description(nameList.get(i))
//                                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
//                                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
//                                        @Override
//                                        public void onSliderClick(BaseSliderView slider) {
//                                            sliderLayout.stopAutoCycle();
//
//                                            Intent intent = new Intent(activity, DetailActivity.class);
//
//                                            intent.putExtra("url_regular", urlList.get(finalI));
//                                            intent.putExtra("id", idList.get(finalI));
//                                            intent.putExtra("color", colorList.get(finalI));
//
//                                            startActivity(intent);
//                                        }
//                                    });
//
//                            sliderView.bundle(new Bundle());
//                            sliderLayout.addSlider(sliderView);
//                        }
//
//                        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
//                        sliderLayout.setCustomAnimation(new DescriptionAnimation());
//                        sliderLayout.addOnPageChangeListener(FragmentNew.this);
//
//                        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
//                        sliderLayout.getPagerIndicator().setDefaultSelectedIndicatorSize(8, 8, PagerIndicator.Unit.DP);
//                        sliderLayout.getPagerIndicator().setDefaultUnselectedIndicatorSize(5, 5, PagerIndicator.Unit.DP);
//                        sliderLayout.getPagerIndicator().setDefaultIndicatorColor(Color.WHITE, Color.WHITE);
//                        sliderLayout.setCurrentPosition(0);
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                sliderLayout.startAutoCycle();
//                            }
//                        }, 300L);
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//
//                    Log.e(TAG, "onResponseNotSuccessful: FragmentNew");
//                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
//
//                    Log.e(TAG, "not success " + response.message());
////                    progressSlider.setVisibility(View.GONE);
//                    swipeRefresh.setRefreshing(false);
//
//                    swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                        @Override
//                        public void onRefresh() {
//                            Log.e(TAG, "onRefresh: 2");
////                            progressSlider.setVisibility(View.VISIBLE);
//                            requestCuratedPhotos(clientId);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
//                Log.e(TAG, "onFailure: FragmentNew");
//                t.printStackTrace();
//
////                sliderLayout.setVisibility(View.GONE);
////                linearLayout.setVisibility(View.VISIBLE);
////                swipeRefresh.setRefreshing(false);
////                progressBar.setVisibility(GONE);
//                Toast.makeText(activity,
//                        getString(R.string.no_internet),
//                        Toast.LENGTH_SHORT)
//                        .show();
//
//                swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
////                        progressSlider.setVisibility(View.VISIBLE);
//                        requestCuratedPhotos(clientId);
//                    }
//                });
//            }
//        });
//    }
}


