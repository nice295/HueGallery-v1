package com.nice295.huegallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.nice295.huegallery.enums.PaletteColorType;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by etiennelawlor on 8/20/15.
 */
public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "NiceGallery-Main";

    private static final int REQUEST_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private FirebaseAnalytics mFirebaseAnalytics;
    private Timer mTimer;
    private TimerTask mTimerTask;

    @Bind(R.id.llRoot)
    LinearLayout llRoot;

    // region Listeners
    public void onViewGalleryButtonClicked() {
        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(LOG_TAG, "Storage permissions has NOT been granted. Requesting permissions.");
            requestStoragePermissions();

        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(LOG_TAG, "Storage permissions have already been granted. Displaying contact details.");

            Intent intent = new Intent(MainActivity.this, ImageGalleryActivity.class);
            ArrayList<String> images = new ArrayList<>();
            images = getDeviceImages();
            intent.putStringArrayListExtra("images", images);
            // optionally set background color using Palette
            intent.putExtra("palette_color_type", PaletteColorType.VIBRANT);
            startActivity(intent);
            finish();
        }
    }
    // endregion

    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                onViewGalleryButtonClicked();
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 2000);
    }

    private ArrayList<String> getDeviceImages() {
        Log.d(LOG_TAG, "Starting image query...");

        ArrayList<String> images = new ArrayList<>();

        String pPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        String projection = "";
        String photoPath = "(1==1)";
        if (pPath.length() > 0) {
            photoPath = " (" + MediaStore.Images.ImageColumns.DATA + " LIKE \'%" + pPath + "%\' )";
        }
        //String groupBy = ") GROUP BY (" + YEAR;
        projection = projection + photoPath; // + grouBy;

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Images.ImageColumns.TITLE,
                        MediaStore.Images.ImageColumns.DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.DATE_TAKEN,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.LATITUDE,
                        MediaStore.Images.ImageColumns.LONGITUDE
                },
                projection,
                null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        if (cursor != null) {
            Log.d(LOG_TAG, "Count of images: " + cursor.getCount());
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                //Log.d(LOG_TAG, "File path: " + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)));
                images.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)));

                cursor.moveToNext();
            }
            cursor.close();
        }
        else {
            Log.d(LOG_TAG, "No images");
        }

        return images;
    }
    // endregion

    private void requestStoragePermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(LOG_TAG, "Displaying storage permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(llRoot, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(MainActivity.this, PERMISSIONS_STORAGE,
                                            REQUEST_STORAGE);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_STORAGE);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_STORAGE) {
        Log.i(LOG_TAG, "Received response for storage permissions request.");

        // We have requested multiple permissions for contacts, so all of them need to be
        // checked.
        if (PermissionUtil.verifyPermissions(grantResults)) {
            /*
            // All required permissions have been granted, display contacts fragment.
            Snackbar.make(llRoot, R.string.permission_available_storage,
                    Snackbar.LENGTH_SHORT)
                    .show();
            */

            Intent intent = new Intent(MainActivity.this, ImageGalleryActivity.class);
            ArrayList<String> images = new ArrayList<>();
            images = getDeviceImages();
            intent.putStringArrayListExtra("images", images);
            // optionally set background color using Palette
            intent.putExtra("palette_color_type", PaletteColorType.VIBRANT);
            startActivity(intent);

        } else {
            Log.i(LOG_TAG, "Storage permissions were NOT granted.");
            Snackbar.make(llRoot, R.string.permissions_not_granted,
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
