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
    private static final String TAG = "wallpapy";
    public static final String QUERY = "query";

    private SearchView searchView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private PagerAdapter pagerAdapter = null;
    private TabSearchPhotos tabSearchPhotos = new TabSearchPhotos();
    private TabSearchUser tabSearchUser = new TabSearchUser();
    private TabSearchCollections tabSearchCollections = new TabSearchCollections();

    private void initView() {
        toolbar = findViewById(R.id.toolbar_search);
        searchView = findViewById(R.id.search_view);
        tabLayout = findViewById(R.id.tab_layout_search);
        viewPager = findViewById(R.id.view_pager_search);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        initView();

        initToolbar();

        initTabAndViewPager();

        searchView.requestFocus();
        SearchView.SearchAutoComplete searchAutoComplete
                = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.white70));
        searchAutoComplete.setTextColor(Color.WHITE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchView.clearFocus();

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
        pagerAdapter.addFragment(tabSearchPhotos);
        pagerAdapter.addFragment(tabSearchUser);
        pagerAdapter.addFragment(tabSearchCollections);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
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
