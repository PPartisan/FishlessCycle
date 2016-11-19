package com.github.ppartisan.fishlesscycle.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class AppUtils {

    private static final String TAG = AppUtils.class.getCanonicalName();
    public static final String FILE_PATH_EXTRA = TAG + ".FILE_PATH_EXTRA";

    private static final String AUTHORITY = "com.github.ppartisan.fishlesscycle.fileprovider";
    private static final SimpleDateFormat TIME_STAMP_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    private static final String FILE_PREFIX = "file:";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int REQUEST_INTERNET = 2;
    private static final String[] PERMISSIONS_INTERNET = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    private AppUtils() { throw new AssertionError(); }

    public static void checkInternetPermissions(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        final int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_NETWORK_STATE
        );

        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_INTERNET,
                    REQUEST_INTERNET
            );
        }

    }

    public static void checkStoragePermissions(Activity activity) {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        final int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
        );

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static Intent buildTakePictureIntent(Activity activity) {

        final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(activity.getPackageManager()) == null) {
            return null;
        }

        File file;
        try{
            file = createImageFile(activity);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        final Uri photoUri = FileProvider.getUriForFile(activity, AUTHORITY, file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        takePictureIntent.putExtra(FILE_PATH_EXTRA, withFilePrefix(file.getAbsolutePath()));
        return takePictureIntent;
    }

    private static File createImageFile(Context context) throws IOException {

        final String timeStamp = TIME_STAMP_FORMAT.format(Calendar.getInstance().getTime());
        final String imageFileName = "JPEG_" + timeStamp + "_";
        final File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);

    }

    public static Intent buildAddToGalleryIntent(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        return  mediaScanIntent;
    }

    public static String withFilePrefix(String path) {
        return FILE_PREFIX + path.trim();
    }

    public static void sendTrackerHit(Tracker tracker, Class c) {
        final String name = c.getSimpleName();
        Log.i("Analytics", "Setting Screen: " + name);
        tracker.setScreenName(name);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
