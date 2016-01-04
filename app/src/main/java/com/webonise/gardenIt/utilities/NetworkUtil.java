package com.webonise.gardenIt.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.webonise.gardenIt.R;

public class NetworkUtil {
    /**
     * A method to check if the network connection is available
     *
     * @param ctx
     * @param showToastErrMsg
     * @return boolean
     */
    public static boolean checkNetwork(Context ctx, boolean showToastErrMsg) {

        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            if (showToastErrMsg) {
                Toast.makeText(ctx, ctx.getString(R.string.toast_no_network), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }
}
