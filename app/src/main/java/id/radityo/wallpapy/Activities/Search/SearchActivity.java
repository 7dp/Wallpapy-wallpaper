package id.radityo.wallpapy.Activities.Search;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import id.radityo.wallpapy.R;

public class SearchActivity extends AppCompatActivity {
    public static final String QUERY = "query";

    private SearchView mSearchView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Toolbar mToolbar;

    private TabSearchPhotos mTabSearchPhotos = new TabSearchPhotos();
    private TabSearchUser mTabSearchUser = new TabSearchUser();
    private TabSearchCollections mTabSearchCollections = new TabSearchCollections();
    private PagerAdapter mPagerAdapter = null;

    private void initView() {
        mToolbar = findViewById(R.id.toolbar_search);
        mSearchView = findViewById(R.id.search_view);
        mTabLayout = findViewById(R.id.tab_layout_search);
        mViewPager = findViewById(R.id.view_pager_search);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        initView();

        initToolbar();

        initTabAndViewPager();

        mSearchView.requestFocus();
        SearchView.SearchAutoComplete searchAutoComplete
                = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.white70));
        searchAutoComplete.setTextColor(Color.WHITE);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                Intent intent = new Intent(QUERY);
                intent.putExtra(QUERY, query);
                sendBroadcast(intent);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void initTabAndViewPager() {
        mPagerAdapter.addFragment(mTabSearchPhotos);
        mPagerAdapter.addFragment(mTabSearchUser);
        mPagerAdapter.addFragment(mTabSearchCollections);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
