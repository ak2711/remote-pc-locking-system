package com.webonise.gardenIt.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class PermissionsUtil {

    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int EXTERNAL_STORAGE_REQUEST_CODE = 2;
    public static final int GALLERY_REQUEST_CODE = 3;

    private PermissionsUtil() {
        //Private constructor to avoid initialization
    }

    public static boolean checkPermissionForCamera(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPermissionForExternalStorage(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission
                .WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
