package com.webonise.gardenIt.utilities;

import android.content.Context;
import android.content.res.Resources;

import com.webonise.gardenIt.R;

public class ColorUtil {

    public static int getColorBasedOnStatus(Context context, String status) {
        Resources res = context.getResources();

        if (status.equalsIgnoreCase(Constants.Status.DONE)) {
            return res.getColor(R.color.status_done);

        } else if (status.equalsIgnoreCase(Constants.Status.PROGRESS)) {
            return res.getColor(R.color.status_progress);

        } else {
            return res.getColor(R.color.status_open);

        }
    }
}
