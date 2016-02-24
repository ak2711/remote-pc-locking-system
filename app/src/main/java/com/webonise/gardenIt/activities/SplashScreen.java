package com.webonise.gardenIt.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.QuickstartPreferences;
import com.webonise.gardenIt.R;

import com.webonise.gardenIt.RegistrationIntentService;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    private boolean isActivityStopped = false;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String TAG = getClass().getName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        isActivityStopped = false;
        Thread sleeper = new Thread(sleepRunnable);
        sleeper.start();
        // Obtain the shared Tracker instance.
        AppController application =  AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.ScreenName.SPLASH_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public Runnable sleepRunnable = new Runnable() {
        public void run() {
            try {
                Thread.sleep(1000); //TODO increase time to 1500 milli secs
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isActivityStopped) {
                goToNextActivity();
            }
        }
    };

    protected void goToNextActivity() {
        Intent intent = new Intent();
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager
                (SplashScreen.this);
        if (sharedPreferenceManager.getBooleanValue(Constants.KEY_PREF_IS_PLANT_ADDED)){
            intent.setClass(SplashScreen.this, DashboardActivity.class);
        } else if (sharedPreferenceManager.getBooleanValue(Constants.KEY_PREF_IS_GARDEN_CREATED)){
            intent.setClass(SplashScreen.this, AddPlantActivity.class);
        } else if (sharedPreferenceManager.getBooleanValue(Constants.KEY_PREF_IS_USER_LOGGED_IN)) {
            intent.setClass(SplashScreen.this, CreateGardenActivity.class);
        } else {
            intent.setClass(SplashScreen.this, SignUpActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityStopped = true;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
