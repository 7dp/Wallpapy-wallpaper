package id.radityo.wallpapy.Activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;
import id.radityo.wallpapy.MyFragment.Collections.FragmentCollections;
import id.radityo.wallpapy.MyFragment.Featured.FragmentFeatured;
import id.radityo.wallpapy.MyFragment.New.FragmentNew;
import id.radityo.wallpapy.R;
import id.radityo.wallpapy.Utils.StatePagerAdapter;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "wallpapy";
    NavigationTabBar navBar;
    ViewPager viewPager;
    public String orderby = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
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
}
