package id.radityo.wallpapy.Fragments.PageIntro;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matthewtamlin.sliding_intro_screen_library.pages.ParallaxPage;

import id.radityo.wallpapy.R;

public class PageIntroFragment extends ParallaxPage {
    private TextView mTvTitle;
    private TextView mTvDescription;
    private ImageView mIvImage;

    private Bitmap mFrontImage;
    private CharSequence mTitle = null;
    private CharSequence mDescription = null;

    public PageIntroFragment() {
        super();
    }

    private void initView(View view) {
        mTvTitle = view.findViewById(R.id.tv_title_page_layout);
        mTvDescription = view.findViewById(R.id.tv_description_page_layout);
        mIvImage = view.findViewById(R.id.iv_front_page_layout);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_layout, container);

        initView(root);

        reflectParametersInView();

        return root;
    }

    public static PageIntroFragment newInstance() {
        return new PageIntroFragment();
    }

    private void reflectParametersInView() {
        if (mIvImage != null) {
            mIvImage.setImageBitmap(null);
            mIvImage.setImageBitmap(mFrontImage);
        }

        if (mTvDescription != null) {
            mTvDescription.setText(null);
            mTvDescription.setText(mDescription);
        }

        if (mTvTitle != null) {
            mTvTitle.setText(null);
            mTvTitle.setText(mTitle);
        }
    }

    public void setFrontImage(final Bitmap frontImage) {
        this.mFrontImage = frontImage;
        reflectParametersInView();
    }

    public Bitmap getFrontImage() {
        return mFrontImage;
    }

    public View getFrontImageHolder() {
        return mIvImage;
    }

    public void setDescription(final CharSequence description) {
        this.mDescription = description;
        reflectParametersInView();
    }

    public CharSequence getDescription() {
        return mDescription;
    }

    public View getDescriptionHolder() {
        return mTvDescription;
    }

    public void setTitle(final CharSequence title) {
        this.mTitle = title;
        reflectParametersInView();
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public View getTitleHolder() {
        return mTvTitle;
    }
}
