package com.webonise.gardenIt.utilities;


import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtil {


    private Context context;

    public DisplayUtil(Context context) {

        this.context = context;
    }

    /**
     * Method to return image height based on 16:9 proportion
     *
     * @return height of image in dp
     */
    public int getImageHeight() {
        int height;
        height = Integer.valueOf((getDeviceWidth() * 9) / 16);
        return height;
    }

    private int pxToDp(int px) {
        return (int) (px / context.getResources().getSystem().getDisplayMetrics().density);
    }

    private int getDeviceWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}
