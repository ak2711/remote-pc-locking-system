package com.webonise.gardenIt.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.webonise.gardenIt.R;

public class GeneralDetailsActivity extends AppCompatActivity implements  View.OnClickListener {

    private final String TAG = this.getClass().getName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);
    }

    @Override
    public void onClick(View v) {

    }
}
