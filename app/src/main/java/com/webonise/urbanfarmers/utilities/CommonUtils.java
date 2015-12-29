package com.webonise.urbanfarmers.utilities;

import android.app.Activity;
import android.content.Context;

public class CommonUtils {

    private Context context;

    public CommonUtils(Context context) {

        this.context = context;
    }

    public int pxToDp(int px) {
        return (int) (px / context.getResources().getSystem().getDisplayMetrics().density);
    }

    public int getDeviceWidth(){
        return ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
    }
}
