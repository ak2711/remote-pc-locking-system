/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webonise.gardenIt;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.webonise.gardenIt.activities.GeneralDetailsActivity;
import com.webonise.gardenIt.utilities.Constants;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String type = data.getString(Constants.BUNDLE_KEY_TYPE);
        String id = data.getString(Constants.BUNDLE_KEY_ID);

        sendNotification(id, type, message);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String id, String type, String message) {
        Intent intent = new Intent(this, GeneralDetailsActivity.class);
        if (type.equalsIgnoreCase(getString(R.string.notification_type_issue))) {
            intent.putExtra(Constants.BUNDLE_KEY_TYPE, Constants.TYPE_ADVICE);
        } else if (type.equalsIgnoreCase(getString(R.string.notification_type_request))) {
            intent.putExtra(Constants.BUNDLE_KEY_TYPE, Constants.TYPE_SERVICE);
        }
        intent.putExtra(Constants.BUNDLE_KEY_ID, Integer.parseInt(id));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(getBitmapDrawable())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private Bitmap getBitmapDrawable() {
        Drawable d = getResources().getDrawable(R.drawable.drawable_notification_icon);
        Bitmap b = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap
                .Config.ARGB_8888);

        LayerDrawable ld = (LayerDrawable) getResources().getDrawable(R.drawable
                .drawable_notification_icon);
        ld.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ld.draw(new Canvas(b));

        return b;
    }
}