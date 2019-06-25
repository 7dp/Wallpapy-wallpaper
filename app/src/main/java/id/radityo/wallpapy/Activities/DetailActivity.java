package id.radityo.wallpapy.Activities;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.radityo.wallpapy.Activities.DetailAuthor.DetailAuthorActivity;
import id.radityo.wallpapy.R;
import id.radityo.wallpapy.Request.APIService;
import id.radityo.wallpapy.Request.ApiClient;
import id.radityo.wallpapy.Utils.Cons;
import id.radityo.wallpapy.Utils.FullScreenDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.SET_WALLPAPER;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static id.radityo.wallpapy.Utils.Cons.CLIENT_ID;
import static id.radityo.wallpapy.Utils.Cons.UTM_PARAM;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "wallpapy";
    private static final int PERMISSION_REQUEST_CODE = 200;

    private String mImageId;
    private String mName;
    private String mProfileLarge;
    private String mBio;
    private String mUserLocation;
    private String mUsername;
    private String mFullResolution;
    private String mProfileSmall;
    private String mMake;
    private String mModel;
    private String mExposureTime;
    private String mAperture;
    private String mFocalLength;
    private String mIso;
    private String mDownloadLinks;
    private String mDownloadLocationLinks;
    private String mHtml;
    private String mRegular;
    private String mColor;
    private String mUserId;
    private int mPermission;
    private int mWidth;
    private int mHeight;
    private int mLikes;
    private int mViews;
    private int mDownloads;
    private boolean mUnreg;
    private long mDownloadId;
    private Uri mUri;

    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;
    private ImageView mIvPhoto;
    private LinearLayout mContainerDesc;
    private LinearLayout mShouldReplace;
    private ImageView mIvAuthor;
    private FloatingActionMenu mFabMenu;
    private FloatingActionButton mFabDownload;
    private FloatingActionButton mFabInfo;
    private FloatingActionButton mFabShare;
    private FloatingActionButton mFabSetWallpaper;
    private FloatingActionButton mFabBrowse;
    private ProgressBar mProgressBar;
    private TextView mTvAuthor;
    private TextView mTvLocation;
    private TextView mTvResolution;
    private TextView mTvColor;
    private TextView mTvDate;
    private TextView mTvDescription;
    private TextView mTvLikes;
    private TextView mTvDownloads;

    private void initViewById() {
        mToolbar = findViewById(R.id.toolbar_photo_detail);
        mIvPhoto = findViewById(R.id.photo_detail);
        mIvAuthor = findViewById(R.id.iv_author_detail);
        mTvAuthor = findViewById(R.id.tv_author_detail);
        mTvLocation = findViewById(R.id.tv_location_detail);
        mTvResolution = findViewById(R.id.tv_resolution_detail);
        mTvColor = findViewById(R.id.tv_color_detail);
        mTvDate = findViewById(R.id.tv_date_detail);
        mTvDescription = findViewById(R.id.tv_description_detail);
        mTvLikes = findViewById(R.id.tv_likes_detail);
        mTvDownloads = findViewById(R.id.tv_downloads_detail);
        mContainerDesc = findViewById(R.id.linear_description);
        mShouldReplace = findViewById(R.id.linear_to_replace);
        mProgressBar = findViewById(R.id.progress_detail);

        mFabMenu = findViewById(R.id.fab_menu);
        mFabInfo = findViewById(R.id.info_item);
        mFabShare = findViewById(R.id.share_item);
        mFabDownload = findViewById(R.id.download_item);
        mFabSetWallpaper = findViewById(R.id.set_wallpaper_item);
        mFabBrowse = findViewById(R.id.browse_item);

        mFabMenu.hideMenuButton(false);
        mShouldReplace.setVisibility(View.GONE);
        mTvLikes.setVisibility(View.GONE);
        mTvDownloads.setVisibility(View.GONE);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(mColor)));
        ab.setElevation(6f);

        if (isColorDark(Color.parseColor(mColor)))
            ab.setHomeAsUpIndicator(R.drawable.ic_back_white_24);
        else ab.setHomeAsUpIndicator(R.drawable.ic_back_black_24);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mImageId = getIntent().getStringExtra("id");
        mColor = getIntent().getStringExtra("color");

        initViewById();

        initToolbar();

        showPhoto();

        requestDetailPhoto(mImageId, CLIENT_ID);

        setupFloatingActionButton();

        lookUpAuthor();
    }

    // FAB actions
    private void setupFloatingActionButton() {
        mFabMenu.setClosedOnTouchOutside(true);
        mFabMenu.setAnimationDelayPerItem(30);

        mFabInfo.setOnClickListener(this);
        mFabDownload.setOnClickListener(this);
        mFabSetWallpaper.setOnClickListener(this);
        mFabShare.setOnClickListener(this);
        mFabBrowse.setOnClickListener(this);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == mDownloadId)
                Toast.makeText(DetailActivity.this, "Download complete!", Toast.LENGTH_LONG).show();
        }
    };

    private long beginDownload(Uri uri, String title) {
        // create directory
        File directory = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + getString(R.string.app_name));
        if (!directory.exists()) directory.mkdirs();

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(title);
        request.setDestinationInExternalPublicDir(Cons.DOWNLOAD_PATH, title.concat(".jpg"));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        long downloadReference = downloadManager.enqueue(request);

        Toast.makeText(DetailActivity.this, "Download started!", Toast.LENGTH_LONG).show();
        return downloadReference;
    }

    private boolean checkPermission() {
        int resultW = ContextCompat.checkSelfPermission(DetailActivity.this, WRITE_EXTERNAL_STORAGE);
        int resultR = ContextCompat.checkSelfPermission(DetailActivity.this, READ_EXTERNAL_STORAGE);
        int resultS = ContextCompat.checkSelfPermission(DetailActivity.this, SET_WALLPAPER);

        return resultW == PackageManager.PERMISSION_GRANTED
                && resultR == PackageManager.PERMISSION_GRANTED
                && resultS == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this, new String[]{
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE,
                        SET_WALLPAPER}, PERMISSION_REQUEST_CODE);
    }

    private void showMessage() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.permission_message))
                .setNeutralButton(getString(R.string.open_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .show();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void showPhoto() {
        String urlRegular = getIntent().getStringExtra("url_regular");
        final Bundle bundle = new Bundle();

        Glide.with(DetailActivity.this)
                .load(urlRegular)
                .thumbnail(0.5F)
                .placeholder(new ColorDrawable(Color.WHITE))
                .error(R.drawable.ic_menu_gallery)
                .fallback(new ColorDrawable(Color.GRAY))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource,
                            @Nullable Transition<? super Drawable> transition) {

                        mIvPhoto.setImageDrawable(resource);

                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                        byte[] bytes = baos.toByteArray();

                        bundle.putByteArray("image", bytes);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        mIvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenDialog dialog = new FullScreenDialog();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager().beginTransaction(), "FullScreenDialog");
                dialog.setCancelable(true);
            }
        });
    }

    private void shareImage() {
        mFabMenu.close(true);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared via Wallpapy");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mHtml + UTM_PARAM);
        startActivity(Intent.createChooser(shareIntent, "Share via: "));
    }

    private Uri getBitmapUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);

        String path = MediaStore.Images.Media.insertImage(
                context.getContentResolver(), bitmap, "Title", "Description");

        return Uri.parse(path);
    }

    private void setWallpaper(Uri uri, ProgressDialog progressDialog) {
        try {
            Intent intent = WallpaperManager
                    .getInstance(DetailActivity.this)
                    .getCropAndSetWallpaperIntent(uri);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("mimeType", "image/*");
            startActivityForResult(intent, 10102);
            progressDialog.dismiss();

        } catch (Exception e) {

            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("mimeType", "image/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, getString(R.string.fab_set_wall)));
            progressDialog.dismiss();
        }
    }

    private void requestDetailPhoto(String imageId, String clientId) {
        APIService service = ApiClient.getBaseUrl();
        Call<ResponseBody> call = service.getDetailPhoto(imageId, clientId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    try {

                        mProgressBar.setVisibility(View.GONE);
                        mShouldReplace.setVisibility(View.VISIBLE);
                        mTvLikes.setVisibility(View.VISIBLE);
                        mTvDownloads.setVisibility(View.VISIBLE);
                        mFabMenu.showMenuButton(true);

                        // ROOT
                        JSONObject rootObj = new JSONObject(response.body().string());

                        String createdAt = rootObj.getString("created_at");
                        mWidth = rootObj.getInt("width");
                        mHeight = rootObj.getInt("height");
                        String color = rootObj.getString("color");
                        String description = rootObj.getString("description");
                        mLikes = rootObj.getInt("likes");
                        mViews = rootObj.getInt("views");
                        mDownloads = rootObj.getInt("downloads");

                        // LINKS
                        JSONObject objLinks = rootObj.getJSONObject("links");
                        mDownloadLinks = objLinks.getString("download");
                        mDownloadLocationLinks = objLinks.getString("download_location");
                        mHtml = objLinks.getString("html");

                        // URLS
                        JSONObject objUrls = rootObj.getJSONObject("urls");
                        mRegular = objUrls.getString("regular");
                        mFullResolution = objUrls.getString("full");

                        // USER
                        JSONObject objAuthor = rootObj.getJSONObject("user");
                        mUserId = objAuthor.getString("id");
                        mUsername = objAuthor.getString("username");
                        mName = objAuthor.getString("name");
                        mBio = objAuthor.getString("bio");
                        mUserLocation = objAuthor.getString("location");

                        // author profile
                        JSONObject profileImage = objAuthor.getJSONObject("profile_image");
                        mProfileSmall = profileImage.getString("small");
                        mProfileLarge = profileImage.getString("large");

                        // EXIF
                        JSONObject exifObj = rootObj.getJSONObject("exif");
                        mMake = exifObj.getString("make");
                        mModel = exifObj.getString("model");
                        mExposureTime = exifObj.getString("exposure_time");
                        mAperture = exifObj.getString("aperture");
                        mFocalLength = exifObj.getString("focal_length");
                        mIso = exifObj.getString("iso");

                        String title_location = null;
                        if (rootObj.has("location")) {
                            JSONObject locationObj = rootObj.getJSONObject("location");
                            title_location = locationObj.getString("title");
                        }

                        // set into view
                        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                        Date date = parser.parse(createdAt);

                        mTvDate.setText(formatter.format(date));
                        mTvAuthor.setText(getString(R.string.by).concat(" ").concat(mName));
                        mTvResolution.setText(beautyFormatter(mWidth).concat(" x ").concat(beautyFormatter(mHeight)));

                        if (description == null || description.equals("null"))
                            mContainerDesc.setVisibility(View.GONE);
                        else mTvDescription.setText(description);

                        mTvLikes.setText(beautyFormatter(mLikes));
                        mTvDownloads.setText(beautyFormatter(mDownloads));

                        if (title_location == null || title_location.equals("null"))
                            mTvLocation.setVisibility(View.GONE);
                        else mTvLocation.setText(title_location);

                        if (color == null || color.equals("null"))
                            mTvColor.setVisibility(View.GONE);
                        else mTvColor.setText(color);

                        Glide.with(getApplicationContext())
                                .load(mProfileLarge)
                                .circleCrop()
                                .thumbnail(0.5F)
                                .error(R.drawable.ic_menu_gallery)
                                .fallback(R.drawable.person_placeholder)
                                .placeholder(new ColorDrawable(Color.WHITE))
                                .into(mIvAuthor);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e(TAG, "onResponseNotSuccessful: DetailActivity");

                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.server_error),
                            Toast.LENGTH_SHORT)
                            .show();

                    mFabMenu.hideMenuButton(false);
                    mProgressBar.setVisibility(View.GONE);
                    mShouldReplace.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: DetailActivity");
                t.printStackTrace();

                mFabMenu.hideMenuButton(false);
                mShouldReplace.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(),
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void lookUpAuthor() {
        mIvAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, DetailAuthorActivity.class);
                intent.putExtra("user_id", mUserId);
                intent.putExtra("name", mName);
                intent.putExtra("user_name", mUsername);
                intent.putExtra("profile_image_small", mProfileSmall);
                intent.putExtra("profile_image_large", mProfileLarge);
                intent.putExtra("location", mUserLocation);
                intent.putExtra("bio", mBio);
                startActivity(intent);
            }
        });

        mTvAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, DetailAuthorActivity.class);
                intent.putExtra("user_id", mUserId);
                intent.putExtra("name", mName);
                intent.putExtra("user_name", mUsername);
                intent.putExtra("profile_image_small", mProfileSmall);
                intent.putExtra("profile_image_large", mProfileLarge);
                intent.putExtra("location", mUserLocation);
                intent.putExtra("bio", mBio);
                startActivity(intent);
            }
        });
    }

    private boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return !(darkness < 0.5);
    }

    private void onFabInfoClick() {
        mFabMenu.close(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        View dView = getLayoutInflater().inflate(R.layout.dialog_info, null);

        TextView tvDimens = dView.findViewById(R.id.dialog_info_tv_dimens);
        TextView tvMake = dView.findViewById(R.id.dialog_info_tv_make);
        TextView tvModel = dView.findViewById(R.id.dialog_info_tv_model);
        TextView tvExposure = dView.findViewById(R.id.dialog_info_tv_exposure);
        TextView tvAperture = dView.findViewById(R.id.dialog_info_tv_aperture);
        TextView tvIso = dView.findViewById(R.id.dialog_info_tv_iso);
        TextView tvFocal = dView.findViewById(R.id.dialog_info_tv_focallength);
        TextView tvViews = dView.findViewById(R.id.dialog_info_tv_views);

        String unknown = getString(R.string.unknown).toLowerCase();

        if (mMake == null || mMake.equals("null")) tvMake.append(" ".concat(unknown));
        else tvMake.append(" ".concat(mMake));

        if (mModel == null || mModel.equals("null")) tvModel.append(" ".concat(unknown));
        else tvModel.append(" ".concat(mModel));

        if (mExposureTime == null || mExposureTime.equals("null"))
            tvExposure.append(" ".concat(unknown));
        else tvExposure.append(" ".concat(mExposureTime));

        if (mAperture == null || mAperture.equals("null")) tvAperture.append(" ".concat(unknown));
        else tvAperture.append(" ".concat(mAperture));

        if (mIso == null || mIso.equals("null")) tvIso.append(" ".concat(unknown));
        else tvIso.append(" ".concat(mIso));

        if (mFocalLength == null || mFocalLength.equals("null"))
            tvFocal.append(" ".concat(unknown));
        else tvFocal.append(" ".concat(mFocalLength));

        tvDimens.append(" ".concat(beautyFormatter(mWidth).concat(" x ").concat(beautyFormatter(mHeight))));

        if (mViews < 2)
            tvViews.setText(String.valueOf(mViews).concat(" ".concat(getString(R.string.single_view))));
        else
            tvViews.setText(beautyFormatter(mViews).concat(" ".concat(getString(R.string.non_single_view))));

        builder.setView(dView);
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
    }

    private void onFABSetWallpaperClick() {
        mFabMenu.close(true);

        mProgressDialog = new ProgressDialog(DetailActivity.this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("Setting up wallpaper...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgressNumberFormat(null);
        mProgressDialog.setProgressPercentFormat(null);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        Glide.with(DetailActivity.this)
                .asBitmap()
                .load(mFullResolution)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap resource,
                            @Nullable Transition<? super Bitmap> transition) {

                        mUri = getBitmapUri(DetailActivity.this, resource);
                        setWallpaper(mUri, mProgressDialog);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                switch (mPermission) {
                    case 1:
                        mFabMenu.close(true);
                        mDownloadId = beginDownload(Uri.parse(mDownloadLinks), mImageId);

                        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                        registerReceiver(broadcastReceiver, filter);
                        mUnreg = true;
                        break;
                    case 2:
                        onFABSetWallpaperClick();
                        break;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)
                            && shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)
                            && shouldShowRequestPermissionRationale(SET_WALLPAPER)) {

                        showMessage();
                    }
                }
            } else {
                showMessage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_item:
                onFabInfoClick();
                break;
            case R.id.share_item:
                shareImage();
                break;
            case R.id.download_item:
                if (!checkPermission()) {
                    requestPermission();
                    mPermission = 1;
                    if (checkPermission()) {
                        mFabMenu.close(true);
                        mDownloadId = beginDownload(Uri.parse(mDownloadLinks), mImageId);

                        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                        registerReceiver(broadcastReceiver, filter);
                        mUnreg = true;
                    }
                } else {
                    mFabMenu.close(true);
                    mDownloadId = beginDownload(Uri.parse(mDownloadLinks), mImageId);

                    IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                    registerReceiver(broadcastReceiver, filter);
                    mUnreg = true;
                }
                break;
            case R.id.set_wallpaper_item:
                if (!checkPermission()) {
                    requestPermission();
                    mPermission = 2;
                    if (checkPermission()) {
                        onFABSetWallpaperClick();
                    }
                } else {
                    onFABSetWallpaperClick();
                }
                break;
            case R.id.browse_item:
                mFabMenu.close(true);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mHtml + UTM_PARAM));
                startActivity(webIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mFabMenu.isOpened()) mFabMenu.close(true);
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnreg) unregisterReceiver(broadcastReceiver);
    }

    public String beautyFormatter(int toFormat) {
        return String.format(Locale.US, "%,d", toFormat);
    }
}
