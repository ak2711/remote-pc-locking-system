package com.webonise.gardenIt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.webonise.gardenIt.R;

import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;

public class SplashScreen extends AppCompatActivity {

    private boolean isActivityStopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread sleeper = new Thread(sleepRunnable);
        sleeper.start();

    }

    public Runnable sleepRunnable = new Runnable() {
        public void run() {
            try {
                Thread.sleep(1000);
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
        if (sharedPreferenceManager.getBooleanValue(Constants.KEY_PREF_IS_USER_LOGGED_IN)) {
            intent.setClass(SplashScreen.this, CreateGardenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            intent.setClass(SplashScreen.this, SignUpActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityStopped = true;
    }

}
