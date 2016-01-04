package com.webonise.gardenIt.utilities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.webonise.gardenIt.AppController;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class ImageUtil {
    private static final String TAG = "Util";

    public static void deleteCapturedPhoto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File directory_dcim = getDefaultCameraDirectory();
                    LogUtils.LOGD(TAG, directory_dcim.getName());
                    File[] files = directory_dcim.listFiles();
                    Arrays.sort(files, new Comparator<File>() {
                        @Override
                        public int compare(File lhs, File rhs) {
                            return Long.valueOf(rhs.lastModified()).compareTo(lhs.lastModified());
                        }
                    });

                    if (files[0].exists() && files[0].isFile()) {
                        files[0].delete();

                        //Function to refresh the gallery after image deletion
                        AppController.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static File getDefaultCameraDirectory() {
        final String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM";
        File file = new File(rootPath + "/Camera");
        if (file.exists() && file.isDirectory())
            return file;
        else {
            file = new File(rootPath + "/100MEDIA");
            if (file.exists() && file.isDirectory())
                return file;
            else {
                file = new File(rootPath + "/100ANDRO");
                if (file.exists() && file.isDirectory())
                    return file;
                else return null;
            }
        }
    }

    public static DisplayImageOptions getImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .considerExifParams(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(500))
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .build();
        return options;
    }

}
