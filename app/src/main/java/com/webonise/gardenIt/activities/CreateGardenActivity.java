package com.webonise.gardenIt.activities;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.CitiesModel;
import com.webonise.gardenIt.models.CreateGardenModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.CommonUtils;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.LogUtils;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateGardenActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferenceManager sharedPreferenceManager;

    private final String TAG = this.getClass().getName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.etNameYourGarden)
    EditText etNameYourGarden;
    @Bind(R.id.btnCreateGarden)
    Button btnCreateGarden;
    @Bind(R.id.etAddressLine1)
    EditText etAddressLine1;
    @Bind(R.id.etAddressLine2)
    EditText etAddressLine2;
    @Bind(R.id.spinnerCity)
    Spinner spinnerCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_garden);
        ButterKnife.bind(this);
        btnCreateGarden.setOnClickListener(this);
        setToolbar();
        getCitiesList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppController application = AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.ScreenName.CREATE_GARDEN_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tvTitle.setText(R.string.create_garden);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateGarden:
                validateAndCreateGarden();
                break;
        }
    }

    private void validateAndCreateGarden() {
        String gardenName = etNameYourGarden.getText().toString().trim();
        String addressLine1 = etAddressLine1.getText().toString().trim();
        String addressLine2 = etAddressLine2.getText().toString().trim();
        String city = spinnerCity.getSelectedItem().toString();
        if (!TextUtils.isEmpty(gardenName)) {
            if (!TextUtils.isEmpty(addressLine1)) {
                createGarden(gardenName, addressLine1, addressLine2, city);
            } else {
                Toast.makeText(CreateGardenActivity.this, getString(R.string.enter_your_address),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CreateGardenActivity.this, getString(R.string.enter_garden_name),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void createGarden(String gardenName, String addressLine1, String addressLine2,
                              String city) {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.CREATE_GARDEN_URL);
        webService.setBody(getBody(gardenName, addressLine1, addressLine2, city));
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                CreateGardenModel createGardenModel = new Gson().fromJson(response,
                        CreateGardenModel.class);
                if (createGardenModel.getStatus() == Constants.RESPONSE_CODE_200) {
                    if (sharedPreferenceManager == null) {
                        sharedPreferenceManager = new SharedPreferenceManager
                                (CreateGardenActivity.this);
                    }
                    sharedPreferenceManager.setBooleanValue(Constants.KEY_PREF_IS_GARDEN_CREATED,
                            true);
                    sharedPreferenceManager.putObject(Constants.KEY_PREF_GARDEN_DETAILS,
                            createGardenModel);
                    sharedPreferenceManager.setIntValue(Constants.KEY_PREF_GARDEN_ID,
                            createGardenModel.getGarden().getId());
                    gotoNextActivity();
                } else {
                    Toast.makeText(CreateGardenActivity.this, createGardenModel.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(CreateGardenActivity.this, getString(R.string.error_msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private JSONObject getBody(String gardenName, String addressLine1, String addressLine2,
                               String city) {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(CreateGardenActivity.this);
        }
        String phoneNumber = sharedPreferenceManager
                .getStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_NAME, gardenName);
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER,
                    phoneNumber);
            jsonObject.put(Constants.REQUEST_KEY_ADDRESS,
                    getFormattedAddress(addressLine1, addressLine2, city));

        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.LOGD(TAG, jsonObject.toString());
        return jsonObject;
    }

    private void gotoNextActivity() {
        Intent intent = new Intent(CreateGardenActivity.this, AddPlantActivity.class);
        startActivity(intent);
        finish();
    }

    private void getCitiesList() {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.GET_CITIES_LIST);
        webService.setBody(getBody());
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                setUpCitiesSpinner(response);
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(CreateGardenActivity.this, getString(R.string.error_msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private JSONObject getBody() {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(CreateGardenActivity.this);
        }
        String phoneNumber = sharedPreferenceManager
                .getStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER,
                    phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.LOGD(TAG, jsonObject.toString());
        return jsonObject;
    }

    private void setUpCitiesSpinner(String response) {
        if (!TextUtils.isEmpty(response)) {
            CitiesModel citiesModel = new Gson().fromJson(response, CitiesModel.class);
            if (citiesModel != null) {
                List cities = citiesModel.getCities();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateGardenActivity.this,
                        android.R.layout.simple_spinner_item, cities);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCity.setAdapter(adapter);
            }
        }
    }

    private String getFormattedAddress(String addressLine1, String addressLine2, String city) {
        StringBuilder formattedAddress = new StringBuilder();
        formattedAddress.append(addressLine1);
        if (!TextUtils.isEmpty(addressLine2)){
            formattedAddress.append(", ").append(addressLine2);
        }
        formattedAddress.append(", ").append(city);
        return formattedAddress.toString();
    }
}

