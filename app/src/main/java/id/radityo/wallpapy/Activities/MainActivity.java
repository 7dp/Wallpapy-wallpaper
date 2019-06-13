package id.radityo.wallpapy.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;
import id.radityo.wallpapy.Activities.Search.SearchActivity;
import id.radityo.wallpapy.MyFragment.Collections.FragmentCollections;
import id.radityo.wallpapy.MyFragment.Featured.FragmentFeatured;
import id.radityo.wallpapy.MyFragment.New.FragmentNew;
import id.radityo.wallpapy.R;
import id.radityo.wallpapy.Utils.StatePagerAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "wallpapy";
    NavigationTabBar navBar;
    ViewPager viewPager;
    public String orderby = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager_main);
        navBar = findViewById(R.id.nav_tab_bar);

        final ActionBar ab = getSupportActionBar();
        ab.setTitle(getString(R.string.newf));

        final StatePagerAdapter pagerAdapter = new StatePagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new FragmentNew());
        pagerAdapter.addFragment(new FragmentFeatured());
        pagerAdapter.addFragment(new FragmentCollections());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        ab.setTitle(getString(R.string.newf));
                        navBar.show();
                        break;
                    case 1:
                        ab.setTitle(getString(R.string.featuredf));
                        navBar.show();
                        break;
                    case 2:
                        ab.setTitle(getString(R.string.collectionsf));
                        navBar.show();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        initNavBar();
    }

    private void initNavBar() {
        ArrayList<NavigationTabBar.Model> modelList = new ArrayList<>();
        int color = getResources().getColor(R.color.orange);

        modelList.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_hot_24),
                        color)
                        .title("New")
                        .build());

        modelList.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_star_24),
                        color)
                        .title("Featured")
                        .build());

        modelList.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_category_24),
                        color)
                        .title("Collections")
                        .build());

        navBar.setModels(modelList);
        navBar.setViewPager(viewPager);
        navBar.setBehaviorEnabled(true);
        navBar.setAnimationDuration(250);
        navBar.setIconSizeFraction(0.4F);
    }

    @Override
    public void onBackPressed() {
        switch (viewPager.getCurrentItem()) {
            case 0:
                super.onBackPressed();
                break;
            case 1:
                viewPager.setCurrentItem(0);
                break;
            case 2:
                viewPager.setCurrentItem(0);
                break;
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete_menu, menu);

        switch (viewPager.getCurrentItem()) {
            case 0:
                // new fragment

                menu.setGroupVisible(R.id.featured_fragment_group, false);
                menu.setGroupVisible(R.id.collections_fragment_group, false);
                menu.setGroupVisible(R.id.new_fragment_group, true);

                MenuItem searchNew = menu.findItem(R.id.action_search_new_featured);

                searchAction(searchNew);

                MenuItem sortLatest = menu.findItem(R.id.action_sort_latest);
                MenuItem sortOldest = menu.findItem(R.id.action_sort_oldest);
                MenuItem sortPopular = menu.findItem(R.id.action_sort_popular);

                itemClickToSort(sortLatest, Constants.LATEST_NEW, NEW);
                itemClickToSort(sortOldest, Constants.OLDEST_NEW, NEW);
                itemClickToSort(sortPopular, Constants.POPULAR_NEW, NEW);

                return true;

            case 1:
                // featured fragment

                menu.setGroupVisible(R.id.new_fragment_group, false);
                menu.setGroupVisible(R.id.collections_fragment_group, false);
                menu.setGroupVisible(R.id.featured_fragment_group, true);

                MenuItem searchFeatured = menu.findItem(R.id.action_search_featured_featured);

                searchAction(searchFeatured);

                MenuItem sortLatestF = menu.findItem(R.id.action_sort_latest_featured);
                MenuItem sortOldestF = menu.findItem(R.id.action_sort_oldest_featured);
                MenuItem sortPopularF = menu.findItem(R.id.action_sort_popular_featured);

                itemClickToSort(sortLatestF, Constants.LATEST_NEW, FEATUREX);
                itemClickToSort(sortOldestF, Constants.OLDEST_NEW, FEATUREX);
                itemClickToSort(sortPopularF, Constants.POPULAR_NEW, FEATUREX);

                return true;

            case 2:
                // collections fragment

                menu.setGroupVisible(R.id.new_fragment_group, false);
                menu.setGroupVisible(R.id.featured_fragment_group, false);
                menu.setGroupVisible(R.id.collections_fragment_group, true);

                MenuItem searchCollections = menu.findItem(R.id.action_search_collections);

                searchAction(searchCollections);

                MenuItem sortAll = menu.findItem(R.id.action_sort_collections_all);
                MenuItem sortCurated = menu.findItem(R.id.action_sort_collections_curated);
                MenuItem sortFeatured = menu.findItem(R.id.action_sort_collections_featured);

                itemClickToSort(sortAll, Constants.ALL, COLLECTIONS);
                itemClickToSort(sortCurated, Constants.CURATED, COLLECTIONS);
                itemClickToSort(sortFeatured, Constants.FEATURED, COLLECTIONS);

                return true;

            default:
                return false;
        }
    }*/

    private void searchAction(MenuItem searchItem) {
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

    private void itemClickToSort(MenuItem sortItem, final String sortBy, final String action) {
        sortItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent(action);
                intent.putExtra("sort_by", sortBy);
                sendBroadcast(intent);

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ACTIVITY");
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Log.e(TAG, "onResumeFragments: ACTIVITY");
    }
}
