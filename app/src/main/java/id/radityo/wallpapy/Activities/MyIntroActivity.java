package id.radityo.wallpapy.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import id.radityo.wallpapy.R;

public class MyIntroActivity extends AppIntro {
    public static final String FIRST_LAUNCH_KEY = "first_launch";
    private static final String[] BACKGROUND_COLORS = {"#383838", "#394281", "#A72929", "#006A6F"};

    private static final int[] DRAWABLE_SLIDER = {
            R.drawable.slider_image_welcome_2,
            R.drawable.slider_image_various_photos,
            R.drawable.slider_image_downloadable,
            R.drawable.slider_image_share_set};

    private String[] mTitles = {
            "Welcome to Wallpapy",
            "Find various photos",
            "Downloadable photos",
            "Share and Set"};

    private String[] mDescriptions = {
            "Thank you for downloading Wallpapy,\nWe hope you like it.\nEnjoy.",
            "Find out amazing image and photos.\nMore than 1M free high-resolution photos and from more than 140,000 photographers around the world.",
            "With curated and popular photos sort you\ncan download any photos you want.",
            "Share with your friends.\nDon't forget that you can set it as wallpaper!"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (int i = 0; i < BACKGROUND_COLORS.length; i++) {

            SliderPage sliderPage = new SliderPage();
            sliderPage.setTitle(mTitles[i]);
            sliderPage.setDescription(mDescriptions[i]);
            sliderPage.setImageDrawable(DRAWABLE_SLIDER[i]);
            sliderPage.setBgColor(Color.parseColor(BACKGROUND_COLORS[i]));
            addSlide(AppIntroFragment.newInstance(sliderPage));
        }

        setDoneText("get started");
        setSkipText("skip");
        setImageNextButton(getResources().getDrawable(R.drawable.ic_forward_24));
        setSeparatorColor(Color.TRANSPARENT);
        showSkipButton(true);
        setProgressButtonEnabled(true);
        showStatusBar(false);
        setFadeAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        getPager().setCurrentItem(fragments.size() - 1);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent getStarted = new Intent(this, MainActivity.class);
        startActivity(getStarted);
        finish();
    }
}
