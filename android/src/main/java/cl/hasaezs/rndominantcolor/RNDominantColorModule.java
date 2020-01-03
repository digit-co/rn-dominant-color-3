package cl.hasaezs.rndominantcolor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.graphics.Palette;
import com.facebook.react.bridge.*;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class RNDominantColorModule extends ReactContextBaseJavaModule {

    private final int defaultColor;

    public RNDominantColorModule(ReactApplicationContext reactContext) {
        super(reactContext);

        this.defaultColor = Color.LTGRAY;
    }

    @Override
    public String getName() {
        return "RNDominantColor";
    }

    private String intColorToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    private WritableMap mapColors(Bitmap bm) {
        Palette palette = Palette.from(bm).generate();
        WritableMap map = Arguments.createMap();

        String dominantColor = intColorToHex(palette.getDominantColor(defaultColor));
        String vibrantColor = intColorToHex(palette.getVibrantColor(defaultColor));
        String lightVibrantColor = intColorToHex(palette.getLightVibrantColor(defaultColor));

        map.putString("dominantColor", dominantColor);
        map.putString("vibrantColor", vibrantColor);
        map.putString("lightVibrantColor", lightVibrantColor);

        return map;
    }

    private void loadImageFromUrl(final String url, final Promise promise) {
        final Activity activity = getCurrentActivity();
        Handler uiHandler = new Handler(Looper.getMainLooper());

        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                WritableMap colorMap = mapColors(bitmap);

                promise.resolve(colorMap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                promise.reject("", "On bitmap failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Picasso
                    .with(activity.getApplicationContext())
                    .load(url)
                    .into(target);
            }
        });
    }

    @ReactMethod
    public void colorsFromUrl(String url, final Promise promise) {
        this.loadImageFromUrl(url, promise);
    }
}