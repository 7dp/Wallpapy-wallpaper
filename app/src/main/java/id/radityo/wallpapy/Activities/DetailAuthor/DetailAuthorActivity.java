package id.radityo.wallpapy.Activities.DetailAuthor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import id.radityo.wallpapy.R;
import jp.wasabeef.blurry.Blurry;

public class DetailAuthorActivity extends AppCompatActivity {
    private ImageView mIvBackground;
    private ImageView mIvForeground;
    private TextView mTvLocation;
    private TextView mTvBio;
    private TextView mTvName;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private CollapsingToolbarLayout mCollapsingLayout;
    private AppBarLayout mAppBar;
    private Drawable mDrawable = null;

    private String mUserId;
    private String mName;
    private String mImageSmall;
    private String mAuthorMedium;
    private String mImageLarge;
    private String mBio;
    private String mLocation;
    private String mUsername;

    private void initView() {
        mIvBackground = findViewById(R.id.iv_background_detail_author_activity);
        mIvForeground = findViewById(R.id.iv_foreground_detail_author_activity);
        mToolbar = findViewById(R.id.toolbar_detail_author);
        mTabLayout = findViewById(R.id.tab_layout_author);
        mViewPager = findViewById(R.id.view_pager_author_detail);
        mTvBio = findViewById(R.id.tv_bio_author_detail);
        mTvLocation = findViewById(R.id.tv_location_author_detail);
        mCollapsingLayout = findViewById(R.id.collapsing_author);
        mTvName = findViewById(R.id.tv_name_author_detail);
        mAppBar = findViewById(R.id.app_bar_author);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_white_24);
        ab.setTitle(mName);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_author);

        mUserId = getIntent().getStringExtra("user_id");
        mName = getIntent().getStringExtra("name");
        mUsername = getIntent().getStringExtra("user_name");
        mImageSmall = getIntent().getStringExtra("profile_image_small");
        mImageLarge = getIntent().getStringExtra("profile_image_large");
        mLocation = getIntent().getStringExtra("location");
        mBio = getIntent().getStringExtra("bio");
        mAuthorMedium = getIntent().getStringExtra("author_med");

        initView();

        initToolbar();

        setDefaultValueProperties();
    }

    private Drawable nullDrawable() {
        Glide.with(DetailAuthorActivity.this)
                .asDrawable()
                .load(mAuthorMedium)
                .fallback(new ColorDrawable(Color.GRAY))
                .error(new ColorDrawable(Color.GRAY))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource,
                            @Nullable Transition<? super Drawable> transition) {
                        mDrawable = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        return mDrawable;
    }

    private void setDefaultValueProperties() {
        if (mImageLarge == null) setCollapsingColor();
        else {
            Glide.with(DetailAuthorActivity.this)
                    .load(mImageLarge)
                    .circleCrop()
                    .error(new ColorDrawable(Color.GRAY))
                    .fallback(nullDrawable())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(mIvForeground);

            setCustomColorAndBlur();
        }

        mTvName.setText(mName);
        if (mBio == null || mBio.equals("null")) mTvBio.setVisibility(View.GONE);
        else mTvBio.setText(mBio.trim());

        if (mLocation == null || mLocation.equals("null")) mTvLocation.setVisibility(View.GONE);
        else mTvLocation.setText(mLocation.trim());

        initTabLayout();

        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                float offsetAlpha = (appBarLayout.getY() / mAppBar.getTotalScrollRange());
                float alpha = 1 - (offsetAlpha * -2.9f);
                mTvBio.setAlpha(alpha);
                mTvName.setAlpha(alpha);
                mTvLocation.setAlpha(alpha);
                mIvForeground.setAlpha(alpha);
            }
        });
    }

    private void setCustomColorAndBlur() {
        Glide.with(DetailAuthorActivity.this)
                .asBitmap()
                .load(mImageLarge)
                .fallback(nullDrawable())
                .error(new ColorDrawable(Color.GRAY))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap resource,
                            @Nullable Transition<? super Bitmap> transition) {

                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@Nullable Palette palette) {
                                Palette.Swatch dominant = palette.getDominantSwatch();
                                mCollapsingLayout.setStatusBarScrimColor(dominant.getRgb());
                                mCollapsingLayout.setContentScrimColor(dominant.getRgb());

                                Window w = getWindow();
                                w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                w.setStatusBarColor(manipulateColor(dominant.getRgb()));
                            }
                        });
                        Blurry.with(DetailAuthorActivity.this)
                                .radius(5)
                                .animate(300)
                                .from(resource)
                                .into(mIvBackground);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void initTabLayout() {
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(pagerAdapter);
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

    public String getUsername() {
        return mUsername;
    }

    private static int manipulateColor(int color) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * 0.85F);
        int g = Math.round(Color.green(color) * 0.85F);
        int b = Math.round(Color.blue(color) * 0.85F);

        return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    private void setCollapsingColor() {
        mCollapsingLayout.setStatusBarScrimColor(Color.BLACK);
        mCollapsingLayout.setContentScrimColor(Color.BLACK);
        mIvBackground.setImageResource(android.R.color.darker_gray);

        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.setStatusBarColor(manipulateColor(Color.BLACK));
    }
}
