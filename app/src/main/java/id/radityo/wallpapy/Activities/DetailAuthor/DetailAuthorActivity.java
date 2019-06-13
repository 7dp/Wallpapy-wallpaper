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
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import id.radityo.wallpapy.BlurTransformation;
import id.radityo.wallpapy.R;
import jp.wasabeef.blurry.Blurry;

public class DetailAuthorActivity extends AppCompatActivity {
    ImageView ivBackground, ivForeground;
    TextView tvLocation, tvBio, tvName;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBar;
    Drawable drawable = null;
    public static final String REQ_PHOTOS = "req_photos";
    private static final String TAG = "wallpapy";

    private String userId, name, imageSmall, authorMed,
            imageLarge, bio, location, username;

    private void findViewById() {
        ivBackground = findViewById(R.id.iv_background_detail_author_activity);
        ivForeground = findViewById(R.id.iv_foreground_detail_author_activity);
        toolbar = findViewById(R.id.toolbar_detail_author);
        tabLayout = findViewById(R.id.tab_layout_author);
        viewPager = findViewById(R.id.view_pager_author_detail);
        tvBio = findViewById(R.id.tv_bio_author_detail);
        tvLocation = findViewById(R.id.tv_location_author_detail);
        collapsingToolbar = findViewById(R.id.collapsing_author);
        tvName = findViewById(R.id.tv_name_author_detail);
        appBar = findViewById(R.id.app_bar_author);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_white_24);
        ab.setTitle(name);
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

        userId = getIntent().getStringExtra("user_id");
        name = getIntent().getStringExtra("name");
        username = getIntent().getStringExtra("user_name");
        imageSmall = getIntent().getStringExtra("profile_image_small");
        imageLarge = getIntent().getStringExtra("profile_image_large");
        location = getIntent().getStringExtra("location");
        bio = getIntent().getStringExtra("bio");
        authorMed = getIntent().getStringExtra("author_med");

        findViewById();

        initToolbar();

        setDefaultValueProperties();
    }

    private Drawable nullDrawable() {
        Glide.with(DetailAuthorActivity.this)
                .asDrawable()
                .load(authorMed)
                .fallback(new ColorDrawable(Color.GRAY))
                .error(new ColorDrawable(Color.GRAY))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource,
                            @Nullable Transition<? super Drawable> transition) {
                        drawable = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        return drawable;
    }

    private void setDefaultValueProperties() {
        if (imageLarge == null) {
            setCollapsingColor();
        } else {
            Glide.with(DetailAuthorActivity.this)
                    .load(imageLarge)
                    .circleCrop()
                    .error(new ColorDrawable(Color.GRAY))
                    .fallback(nullDrawable())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivForeground);

            setCustomColorAndBlur();
        }

        BlurTransformation blurTransformation = new BlurTransformation(DetailAuthorActivity.this, 50F);
        MultiTransformation<Bitmap> multiTrans = new MultiTransformation<>(blurTransformation, new CenterCrop());

        tvName.setText(name);
        if (bio == null || bio.equals("null"))
            tvBio.setVisibility(View.GONE);
        else
            tvBio.setText(bio.trim());

        if (location == null || location.equals("null"))
            tvLocation.setVisibility(View.GONE);
        else
            tvLocation.setText(location.trim());

        initTabLayout();

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                float offsetAlpha = (appBarLayout.getY() / appBar.getTotalScrollRange());
                float alpha = 1 - (offsetAlpha * -2.9f);
                tvBio.setAlpha(alpha);
                tvName.setAlpha(alpha);
                tvLocation.setAlpha(alpha);
                ivForeground.setAlpha(alpha);
            }
        });
    }

    private void setCustomColorAndBlur() {
        Glide.with(DetailAuthorActivity.this)
                .asBitmap()
                .load(imageLarge)
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
                                collapsingToolbar.setStatusBarScrimColor(dominant.getRgb());
                                collapsingToolbar.setContentScrimColor(dominant.getRgb());

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
                                .into(ivBackground);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void initTabLayout() {
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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

    public String getUsername() {
        return username;
    }

    private static int manipulateColor(int color) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * 0.85F);
        int g = Math.round(Color.green(color) * 0.85F);
        int b = Math.round(Color.blue(color) * 0.85F);

        return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    private void setCollapsingColor() {
        collapsingToolbar.setStatusBarScrimColor(Color.BLACK);
        collapsingToolbar.setContentScrimColor(Color.BLACK);
        ivForeground.setImageResource(R.drawable.person_placeholder);
        ivBackground.setImageResource(android.R.color.darker_gray);

        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.setStatusBarColor(manipulateColor(Color.BLACK));
    }

}
