package com.nice295.huegallery.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nice295.huegallery.R;
import com.nice295.huegallery.enums.PaletteColorType;
import com.nice295.huegallery.util.ImageGalleryUtils;
import com.nice295.huegallery.view.PaletteTransformation;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by etiennelawlor on 8/20/15.
 */
public class FullScreenImageGalleryAdapter extends PagerAdapter {

    private static final String LOG_TAG = "FullScreenImageGalleryAdapter";
    // region Member Variables
    private final List<String> mImages;
    private final PaletteColorType mPaletteColorType;
    // endregion

    // region Constructors
    public FullScreenImageGalleryAdapter(List<String> images, PaletteColorType paletteColorType) {
        mImages = images;
        mPaletteColorType = paletteColorType;
    }
    // endregion

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.fullscreen_image, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv);
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll);
        final LinearLayout llToolbar = (LinearLayout) view.findViewById(R.id.llToolbar); //khlee

        String image = mImages.get(position);

        /* khlee
        if (!TextUtils.isEmpty(image)) {
            Picasso.with(imageView.getContext())
                    .load(image)
                    .transform(PaletteTransformation.instance())
                    .into(imageView, new PaletteTransformation.PaletteCallback(imageView) {
                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onSuccess(Palette palette) {
                            int bgColor = getBackgroundColor(palette);
                            if (bgColor != -1)
                                linearLayout.setBackgroundColor(bgColor);
                        }
                    });
        } else {
            imageView.setImageDrawable(null);
        }
        */
        if (!TextUtils.isEmpty(image)) {
            Picasso.with(imageView.getContext())
                    .load(image)
                    .resize(ImageGalleryUtils.getScreenWidth(imageView.getContext()), ImageGalleryUtils.getScreenHeight(imageView.getContext()))
                    .transform(PaletteTransformation.instance())
                    .centerInside()
                    .into(imageView, new PaletteTransformation.PaletteCallback(imageView) {
                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onSuccess(Palette palette) {
                            int bgColor = getBackgroundColor(palette);
                            if (bgColor != -1)
                                linearLayout.setBackgroundColor(bgColor);
                                llToolbar.setBackgroundColor(bgColor);
                                updateLights(bgColor);
                        }
                    });
        } else {
            imageView.setImageDrawable(null);
        }


        container.addView(view, 0);

        return view;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // region Helper Methods
    private int getBackgroundColor(Palette palette) {
        int bgColor = -1;

        int vibrantColor = palette.getVibrantColor(0x000000);
        int lightVibrantColor = palette.getLightVibrantColor(0x000000);
        int darkVibrantColor = palette.getDarkVibrantColor(0x000000);

        int mutedColor = palette.getMutedColor(0x000000);
        int lightMutedColor = palette.getLightMutedColor(0x000000);
        int darkMutedColor = palette.getDarkMutedColor(0x000000);

        if (mPaletteColorType != null) {
            switch (mPaletteColorType) {
                case VIBRANT:
                    if (vibrantColor != 0) { // primary option
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) { // fallback options
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case LIGHT_VIBRANT:
                    if (lightVibrantColor != 0) { // primary option
                        bgColor = lightVibrantColor;
                    } else if (vibrantColor != 0) { // fallback options
                        bgColor = vibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case DARK_VIBRANT:
                    if (darkVibrantColor != 0) { // primary option
                        bgColor = darkVibrantColor;
                    } else if (vibrantColor != 0) { // fallback options
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case MUTED:
                    if (mutedColor != 0) { // primary option
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) { // fallback options
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                case LIGHT_MUTED:
                    if (lightMutedColor != 0) { // primary option
                        bgColor = lightMutedColor;
                    } else if (mutedColor != 0) { // fallback options
                        bgColor = mutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                case DARK_MUTED:
                    if (darkMutedColor != 0) { // primary option
                        bgColor = darkMutedColor;
                    } else if (mutedColor != 0) { // fallback options
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                default:
                    break;
            }
        }

        return bgColor;
    }
    // endregion

    public void updateLights(Integer color) {
        PHBridge bridge = PHHueSDK.getInstance().getSelectedBridge();

        if (bridge == null)
            return;

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            float xy[] = PHUtilities.calculateXYFromRGB( Color.red(color),  Color.green(color),  Color.blue(color), light.getModelNumber());
            lightState.setX(xy[0]);
            lightState.setY(xy[1]);
            // To validate your lightstate is valid (before sending to the bridge) you can use:
            // String validState = lightState.validateState();
            bridge.updateLightState(light, lightState, listener);
            //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
        }
    }

    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {

        @Override
        public void onSuccess() {
            Log.w(LOG_TAG, "onSuccess");
        }

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
            Log.w(LOG_TAG, "Light has updated");
        }

        @Override
        public void onError(int arg0, String arg1) {
            Log.w(LOG_TAG, "Light has error");
        }

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };
}
