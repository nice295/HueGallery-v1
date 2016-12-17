package com.nice295.huegallery;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nice295.huegallery.adapters.ImageGalleryAdapter;
import com.nice295.huegallery.enums.PaletteColorType;
import com.nice295.huegallery.philips.lighting.quickstart.PHHomeActivity;
import com.nice295.huegallery.util.ImageGalleryUtils;
import com.nice295.huegallery.view.GridSpacesItemDecoration;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ImageGalleryActivity extends AppCompatActivity implements ImageGalleryAdapter.OnImageClickListener {
    private static final String LOG_TAG = "ImageGalleryActivity";
    private PHHueSDK phHueSDK;
    private Boolean mShowSnackbar = false;

    // region Member Variables
    private ArrayList<String> mImages;
    private PaletteColorType mPaletteColorType;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    // endregion

    @Override
    protected void onResume() {
        super.onResume();

        /*
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null && !mShowSnackbar) {
            Snackbar.make(mRecyclerView, R.string.bridge_connected,
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    })
                    .show();
            mShowSnackbar = true;
        }
        else {
            mShowSnackbar = false;
            Snackbar.make(mRecyclerView, R.string.bridge_disconnected,
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), PHHomeActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
        */

    }

    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_gallery);

        bindViews();

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.HueGallery);
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mImages = extras.getStringArrayList("images");
                mPaletteColorType = (PaletteColorType) extras.get("palette_color_type");
            }
        }

        setUpRecyclerView();

        phHueSDK = PHHueSDK.create();

        // from resume()
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null && !mShowSnackbar) {
            Snackbar.make(mRecyclerView, R.string.bridge_connected,
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    })
                    .show();
            mShowSnackbar = true;
        }
        else {
            mShowSnackbar = false;
            Snackbar.make(mRecyclerView, R.string.bridge_disconnected,
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_settings, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), PHHomeActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }
    // endregion

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {

            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }

            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_imagegallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PHHomeActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUpRecyclerView();
    }

    // region ImageGalleryAdapter.OnImageClickListener Methods
    @Override
    public void onImageClick(int position) {
        Intent intent = new Intent(ImageGalleryActivity.this, FullScreenImageGalleryActivity.class);

        intent.putStringArrayListExtra("images", mImages);
        intent.putExtra("position", position);
        if(mPaletteColorType != null){
            intent.putExtra("palette_color_type", mPaletteColorType);
        }

        startActivity(intent);
    }
    // endregion

    // region Helper Methods
    private void bindViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setUpRecyclerView(){
        int numOfColumns;
        if(ImageGalleryUtils.isInLandscapeMode(this)){
            numOfColumns = 4;
        } else {
            numOfColumns = 3;
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(ImageGalleryActivity.this, numOfColumns));
        mRecyclerView.addItemDecoration(new GridSpacesItemDecoration(ImageGalleryUtils.dp2px(this, 2), numOfColumns));
        ImageGalleryAdapter imageGalleryAdapter = new ImageGalleryAdapter(mImages);
        imageGalleryAdapter.setOnImageClickListener(this);

        mRecyclerView.setAdapter(imageGalleryAdapter);
    }
    // endregion
}
