package com.webonise.gardenIt.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import com.webonise.gardenIt.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareUtil {
    private Context context;
    private File file;

    public ShareUtil(Context context) {
        this.context = context;
    }

    public void shareContent(Uri filePath) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, filePath);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent,
                context.getResources().getText(R.string.share)));
    }

    /**
     * Returns the URI path to the Bitmap displayed in specified ImageView
     *
     * @param imageView
     * @return uri of the image path
     */
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "gardenIt_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public void deleteImageFile() {
        if (file != null && file.exists()){
            file.delete();
        }
    }
}
