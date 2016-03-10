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
     * Method to return image height
     *
     * @param proportionType
     * @return height of image in dp
     */
    public int getImageHeight(int proportionType) {
        int height;
        switch (proportionType) {
            case Constants.PROPORTION_TYPE.ONE_BY_THREE:
                return Integer.valueOf(getDeviceWidth() / 3);

            case Constants.PROPORTION_TYPE.NINE_BY_SIXTEEN:
                return Integer.valueOf((getDeviceWidth() * 9) / 16);

            default:
                return getDeviceHeight();
        }
    }

    public int pxToDp(int px) {
        return (int) (px / context.getResources().getSystem().getDisplayMetrics().density);
    }

    public int dpToPx(int dp) {
        return (int) (dp * context.getResources().getSystem().getDisplayMetrics().density);
    }

    private int getDeviceWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    private int getDeviceHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }
}
