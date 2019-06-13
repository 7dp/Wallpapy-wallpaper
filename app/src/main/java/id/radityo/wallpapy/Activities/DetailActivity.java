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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
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
import id.radityo.wallpapy.FullScreenDialog;
import id.radityo.wallpapy.R;
import id.radityo.wallpapy.Request.APIService;
import id.radityo.wallpapy.Request.ApiClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.SET_WALLPAPER;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static id.radityo.wallpapy.Constants.CLIENT_ID;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "wallpapy";
    private static final String UTM_PARAM = "?utm_source=resplash&utm_medium=referral&utm_campaign=api-credit";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private String imageId, name, profile_medium,
            profile_large, bio, user_location,
            username, full, profile_small;
    boolean unreg;
    int permission;
    Uri uri;

    ProgressDialog progressDialog;
    Toolbar toolbar;
    ImageView ivPhoto;
    LinearLayout containerDesc, shouldReplace;
    RelativeLayout containerAuthor;
    ImageView ivAuthor;
    FloatingActionMenu fabMenu;
    FloatingActionButton fabDownload, fabInfo,
            fabShare, fabSetWall, fabBrowse;
    ProgressBar progressBar;
    TextView tvAuthor, tvLocation, tvResolution,
            tvColor, tvDate, tvDescription,
            tvLikes, tvDownloads;
    String make, model, exposure_time,
            aperture, focal_length, iso,
            download_links, download_location_links,
            html, regular, color;
    String user_id;
    int width, height, likes, views, downloads;
    long downloadId;

    private void initViewById() {
        toolbar = findViewById(R.id.toolbar_photo_detail);
        ivPhoto = findViewById(R.id.photo_detail);
        ivAuthor = findViewById(R.id.iv_author_detail);
        tvAuthor = findViewById(R.id.tv_author_detail);
        tvLocation = findViewById(R.id.tv_location_detail);
        tvResolution = findViewById(R.id.tv_resolution_detail);
        tvColor = findViewById(R.id.tv_color_detail);
        tvDate = findViewById(R.id.tv_date_detail);
        tvDescription = findViewById(R.id.tv_description_detail);
        tvLikes = findViewById(R.id.tv_likes_detail);
        tvDownloads = findViewById(R.id.tv_downloads_detail);
        containerDesc = findViewById(R.id.linear_description);
        shouldReplace = findViewById(R.id.linear_to_replace);
        progressBar = findViewById(R.id.progress_detail);
        fabMenu = findViewById(R.id.fab_menu);
        fabInfo = findViewById(R.id.info_item);
        fabShare = findViewById(R.id.share_item);
        fabDownload = findViewById(R.id.download_item);
        fabSetWall = findViewById(R.id.set_wallpaper_item);
        fabBrowse = findViewById(R.id.browse_item);
        containerAuthor = findViewById(R.id.container_author_detail);

        shouldReplace.setVisibility(View.GONE);
        fabMenu.hideMenuButton(false);
        tvLikes.setVisibility(View.GONE);
        tvDownloads.setVisibility(View.GONE);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(6f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));

        if (isColorDark(Color.parseColor(color)))
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_24);
        else
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_black_24);
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

        imageId = getIntent().getStringExtra("id");
        color = getIntent().getStringExtra("color");
        Log.e(TAG, "imageId: " + imageId);

        initViewById();

        initToolbar();

        showPhoto();

        requestDetailPhoto(imageId, CLIENT_ID);

        setFloatingActionButton();

        lookUpAuthor();
    }

    // FAB actions
    private void setFloatingActionButton() {
        fabMenu.setClosedOnTouchOutside(true);
        fabMenu.setAnimationDelayPerItem(50);
        fabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFABInfoClick();
            }
        });

        fabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    requestPermission();
                    permission = 1;
                    if (checkPermission()) {
                        fabMenu.close(true);
                        downloadId = beginDownload(Uri.parse(download_links), imageId);

                        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                        registerReceiver(broadcastReceiver, filter);
                        unreg = true;
                    }
                } else {
                    fabMenu.close(true);
                    downloadId = beginDownload(Uri.parse(download_links), imageId);

                    IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                    registerReceiver(broadcastReceiver, filter);
                    unreg = true;
                }
            }
        });

        fabSetWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    requestPermission();
                    permission = 2;
                    if (checkPermission()) {
                        onFABSetWallpaperClick();
                    }
                } else {
                    onFABSetWallpaperClick();
                }
            }
        });

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.close(true);
                shareImage();
            }
        });

        fabBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.close(true);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(html + UTM_PARAM));
                startActivity(webIntent);
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Log.e(TAG, "id: " + id);

            if (id == downloadId) {
                Toast.makeText(DetailActivity.this, "Download complete!", Toast.LENGTH_LONG).show();
            }
        }
    };

    private long beginDownload(Uri uri, String title) {
        // create directory
        File directory = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).toString() + File.separator + getString(R.string.app_name));
        if (!directory.exists())
            directory.mkdirs();

        long downloadReference;

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(title);
        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name), title.concat(".jpg"));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        downloadReference = downloadManager.enqueue(request);
        Log.e(TAG, "download reference: " + downloadReference);

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
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
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
        Log.e(TAG, "url: " + urlRegular);
        final Bundle bundle = new Bundle();

        Glide.with(DetailActivity.this)
                .load(urlRegular)
                .thumbnail(0.5F)
                .error(R.drawable.ic_menu_gallery)
                .fallback(R.drawable.ic_menu_gallery)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Drawable resource,
                            @Nullable Transition<? super Drawable> transition) {

                        ivPhoto.setImageDrawable(resource);

                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] bytes = baos.toByteArray();

                        bundle.putByteArray("image", bytes);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
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
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared via Wallpapy");
        shareIntent.putExtra(Intent.EXTRA_TEXT, html + UTM_PARAM);
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
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(DetailActivity.this.getContentResolver(), uri);
//            WallpaperManager.getInstance(DetailActivity.this).setBitmap(bitmap);
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

                if (response.isSuccessful() /*&& isGranted*/) {

                    try {

                        Log.e(TAG, "onResponse: step 1");
                        progressBar.setVisibility(View.GONE);
                        shouldReplace.setVisibility(View.VISIBLE);
                        fabMenu.showMenuButton(true);
                        tvLikes.setVisibility(View.VISIBLE);
                        tvDownloads.setVisibility(View.VISIBLE);

                        // ROOT
                        JSONObject rootObj = new JSONObject(response.body().string());

                        String id = rootObj.getString("id");
                        String createdAt = rootObj.getString("created_at");
                        String updatedAt = rootObj.getString("updated_at");
                        width = rootObj.getInt("width");
                        height = rootObj.getInt("height");
                        String color = rootObj.getString("color");
                        String description = rootObj.getString("description");
                        String alternateDescription = rootObj.getString("alt_description");
                        JSONArray categories = rootObj.getJSONArray("categories");
                        boolean sponsored = rootObj.getBoolean("sponsored");
                        String sponsoredBy = rootObj.getString("sponsored_by");
                        likes = rootObj.getInt("likes");
                        views = rootObj.getInt("views");
                        downloads = rootObj.getInt("downloads");

                        // LINKS
                        JSONObject objLinks = rootObj.getJSONObject("links");
                        download_links = objLinks.getString("download");
                        download_location_links = objLinks.getString("download_location");
                        html = objLinks.getString("html");

                        // URLS
                        JSONObject objUrls = rootObj.getJSONObject("urls");
                        regular = objUrls.getString("regular");
                        full = objUrls.getString("full");

                        // USER
                        JSONObject objAuthor = rootObj.getJSONObject("user");
                        user_id = objAuthor.getString("id");
                        username = objAuthor.getString("username");
                        name = objAuthor.getString("name");
                        bio = objAuthor.getString("bio");
                        user_location = objAuthor.getString("location");

                        // author links
                        JSONObject authorLinksObject = objAuthor.getJSONObject("links");

                        // author profile
                        JSONObject profileImage = objAuthor.getJSONObject("profile_image");
                        profile_small = profileImage.getString("small");
                        profile_medium = profileImage.getString("medium");
                        profile_large = profileImage.getString("large");

                        // EXIF
                        JSONObject exifObj = rootObj.getJSONObject("exif");
                        make = exifObj.getString("make");
                        model = exifObj.getString("model");
                        exposure_time = exifObj.getString("exposure_time");
                        aperture = exifObj.getString("aperture");
                        focal_length = exifObj.getString("focal_length");
                        iso = exifObj.getString("iso");

                        String title_location = null;
                        if (rootObj.has("location")) {
                            JSONObject locationObj = rootObj.getJSONObject("location");
                            title_location = locationObj.getString("title");
                        }

                        Log.e(TAG, "onResponse: step 2");
                        // set into view
                        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
                        Date date = parser.parse(createdAt);

                        Log.e(TAG, "date: " + createdAt);
                        Log.e(TAG, "name: " + name);
                        Log.e(TAG, "res: " + width + " " + height);
                        Log.e(TAG, "color: " + color);

                        tvDate.setText(formatter.format(date));
                        tvAuthor.setText(getString(R.string.by).concat(" ").concat(name));
                        tvResolution.setText(beautyFormatter(width).concat(" x ").concat(beautyFormatter(height)));

                        if (description == null || description.equals("null"))
                            containerDesc.setVisibility(View.GONE);
                        else
                            tvDescription.setText(description);

                        tvLikes.setText(beautyFormatter(likes));

                        tvDownloads.setText(beautyFormatter(downloads));

                        if (title_location == null || title_location.equals("null"))
                            tvLocation.setVisibility(View.GONE);
                        else tvLocation.setText(title_location);

                        if (color == null || color.equals("null"))
                            tvColor.setVisibility(View.GONE);
                        else tvColor.setText(color);

                        Glide.with(getApplicationContext())
                                .load(profile_medium)
                                .circleCrop()
                                .thumbnail(0.5F)
                                .error(R.drawable.ic_menu_gallery)
                                .fallback(new ColorDrawable(Color.GRAY))
                                .placeholder(new ColorDrawable(Color.WHITE))
                                .into(ivAuthor);

                        Log.e(TAG, "onResponse: step 3");

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

                    fabMenu.hideMenuButton(false);
                    progressBar.setVisibility(View.GONE);
                    shouldReplace.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: DetailActivity");
                t.printStackTrace();

                fabMenu.hideMenuButton(false);
                shouldReplace.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void lookUpAuthor() {
        ivAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, DetailAuthorActivity.class);

                intent.putExtra("user_id", user_id);
                intent.putExtra("name", name);
                intent.putExtra("user_name", username);
                intent.putExtra("profile_image_small", profile_small);
                intent.putExtra("profile_image_large", profile_large);
                intent.putExtra("location", user_location);
                intent.putExtra("bio", bio);

                startActivity(intent);
            }
        });

        tvAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, DetailAuthorActivity.class);

                intent.putExtra("user_id", user_id);
                intent.putExtra("name", name);
                intent.putExtra("user_name", username);
                intent.putExtra("profile_image_small", profile_small);
                intent.putExtra("profile_image_large", profile_large);
                intent.putExtra("location", user_location);
                intent.putExtra("bio", bio);

                startActivity(intent);
            }
        });
    }

    private boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return !(darkness < 0.5);
    }

    private void onFABInfoClick() {
        fabMenu.close(true);

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

        String unknow = getString(R.string.unknown).toLowerCase();

        if (make == null || make.equals("null"))
            tvMake.append(" ".concat(unknow));
        else tvMake.append(" ".concat(make));

        if (model == null || model.equals("null"))
            tvModel.append(" ".concat(unknow));
        else tvModel.append(" ".concat(model));

        if (exposure_time == null || exposure_time.equals("null"))
            tvExposure.append(" ".concat(unknow));
        else tvExposure.append(" ".concat(exposure_time));

        if (aperture == null || aperture.equals("null"))
            tvAperture.append(" ".concat(unknow));
        else tvAperture.append(" ".concat(aperture));

        if (iso == null || iso.equals("null"))
            tvIso.append(" ".concat(unknow));
        else tvIso.append(" ".concat(iso));

        if (focal_length == null || focal_length.equals("null"))
            tvFocal.append(" ".concat(unknow));
        else tvFocal.append(" ".concat(focal_length));

        tvDimens.append(" ".concat(beautyFormatter(width).concat(" x ").concat(beautyFormatter(height))));

        if (views < 2) {
            tvViews.setText(String.valueOf(views).concat(" ".concat(getString(R.string.single_view))));
        } else {
            tvViews.setText(beautyFormatter(views).concat(" ".concat(getString(R.string.non_single_view))));
        }

        Log.e(TAG, "make: " + make);
        Log.e(TAG, "model: " + model);
        Log.e(TAG, "exposure_time: " + exposure_time);
        Log.e(TAG, "aperture: " + aperture);
        Log.e(TAG, "focal_length: " + focal_length);
        Log.e(TAG, "iso: " + iso);
        Log.e(TAG, "w x h: " + width + " x " + height);

        builder.setView(dView);
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
    }

    private void onFABSetWallpaperClick() {
        fabMenu.close(true);

        progressDialog = new ProgressDialog(DetailActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Setting wallpaper...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        Glide.with(DetailActivity.this)
                .asBitmap()
                .load(full)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap resource,
                            @Nullable Transition<? super Bitmap> transition) {

                        uri = getBitmapUri(DetailActivity.this, resource);
                        Log.e(TAG, "bitmap: " + resource);
                        setWallpaper(uri, progressDialog);
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

                Log.e(TAG, "onRequestPermissionsResult: GRANTED");

                switch (permission) {
                    case 1:
                        fabMenu.close(true);
                        downloadId = beginDownload(Uri.parse(download_links), imageId);

                        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                        registerReceiver(broadcastReceiver, filter);
                        unreg = true;
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
                Log.e(TAG, "onRequestPermissionsResult: DENIED");
            }
        } else {
            Log.e(TAG, "onRequestPermissionsResult: ");
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }

    @Override
    public void onBackPressed() {
        if (fabMenu.isOpened()) {
            fabMenu.close(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unreg) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    public String beautyFormatter(int toFormat) {
        return String.format(Locale.US, "%,d", toFormat);
    }
}
