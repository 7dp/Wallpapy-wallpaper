package id.radityo.wallpapy.Activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;
import id.radityo.wallpapy.Fragments.Collections.FragmentCollections;
import id.radityo.wallpapy.Fragments.Featured.FragmentFeatured;
import id.radityo.wallpapy.Fragments.New.FragmentNew;
import id.radityo.wallpapy.R;
import id.radityo.wallpapy.Utils.StatePagerAdapter;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    NavigationTabBar mNavigationBar;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.view_pager_main);
        mNavigationBar = findViewById(R.id.nav_tab_bar);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setTitle(getString(R.string.newf));

        final StatePagerAdapter pagerAdapter = new StatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new FragmentNew());
        pagerAdapter.addFragment(new FragmentFeatured());
        pagerAdapter.addFragment(new FragmentCollections());

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        ab.setTitle(getString(R.string.newf));
                        mNavigationBar.show();
                        break;
                    case 1:
                        ab.setTitle(getString(R.string.featuredf));
                        mNavigationBar.show();
                        break;
                    case 2:
                        ab.setTitle(getString(R.string.collectionsf));
                        mNavigationBar.show();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        initNavigationBar();
    }

    private void initNavigationBar() {
        ArrayList<NavigationTabBar.Model> modelList = new ArrayList<>();
        int orange = getResources().getColor(R.color.orange);

        modelList.add(new NavigationTabBar.Model.Builder(
                getResources().getDrawable(R.drawable.ic_hot_24),
                orange)
                .title("New")
                .build());

        modelList.add(new NavigationTabBar.Model.Builder(
                getResources().getDrawable(R.drawable.ic_star_24),
                orange)
                .title("Featured")
                .build());

        modelList.add(new NavigationTabBar.Model.Builder(
                getResources().getDrawable(R.drawable.ic_category_24),
                orange)
                .title("Collections")
                .build());

        mNavigationBar.setModels(modelList);
        mNavigationBar.setViewPager(mViewPager);
        mNavigationBar.setBehaviorEnabled(true);
        mNavigationBar.setAnimationDuration(250);
        mNavigationBar.setIconSizeFraction(0.43F);
    }

    @Override
    public void onBackPressed() {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                super.onBackPressed();
                break;
            case 1:
                mViewPager.setCurrentItem(0);
                break;
            case 2:
                mViewPager.setCurrentItem(0);
                break;
        }
    }
}
