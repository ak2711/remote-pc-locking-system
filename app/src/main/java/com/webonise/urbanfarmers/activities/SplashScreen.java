package com.webonise.urbanfarmers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.webonise.urbanfarmers.R;
import com.webonise.urbanfarmers.utilities.Constants;
import com.webonise.urbanfarmers.utilities.SharedPreferenceManager;

public class SplashScreen extends AppCompatActivity {

    private boolean isActivityStopped = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(SplashScreen.this);
        if (sharedPreferenceManager.getBooleanValue(Constants.KEY_PREF_IS_USER_LOGGED_IN)) {
            //intent.setClass(SplashScreen.this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            //intent.setClass(SplashScreen.this, WalkThroughActivity.class);
        }
        //startActivity(intent);
        //finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityStopped = true;
    }

}
