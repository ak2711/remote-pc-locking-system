package com.webonise.gardenIt.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.SignUpRequestModel;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.models.UserDetailsUpdateRequestModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.FileContentProvider;
import com.webonise.gardenIt.utilities.ImageUtil;
import com.webonise.gardenIt.utilities.LogUtils;
import com.webonise.gardenIt.utilities.ShareUtil;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.utilities.UriManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class UpdatePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.etPassword)
    EditText etPassword;
    @Bind(R.id.etNewPassword)
    EditText etNewPassword;
    @Bind(R.id.etConfirmPassword)
    EditText etConfirmPassword;
    @Bind(R.id.btnUpdatePassword)
    Button btnUpdatePassword;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        setToolbar();
        btnUpdatePassword.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Obtain the shared Tracker instance.
        AppController application = AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.ScreenName.UPDATE_PASSWORD_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(R.drawable.ic_action_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            tvTitle.setText(R.string.update_password);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdatePassword:
                validateAndUpdate();
                break;

        }
    }

    private void validateAndUpdate() {
        String oldPassword = etPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phoneNumber = new SharedPreferenceManager(this).getStringValue(Constants
                .KEY_PREF_USER_PHONE_NUMBER);
        if (!TextUtils.isEmpty(oldPassword)) {
            if (!TextUtils.isEmpty(newPassword)) {
                if (newPassword.equals(confirmPassword)) {
                    updatePassword(phoneNumber, oldPassword, newPassword);
                } else {
                    Toast.makeText(UpdatePasswordActivity.this,
                            getString(R.string.password_does_not_match),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(UpdatePasswordActivity.this, getString(R.string.enter_new_password),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(UpdatePasswordActivity.this, getString(R.string.enter_password),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePassword(final String phoneNumber, String oldPassword, final String newPassword) {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.UPDATE_PASSWORD_URL);
        webService.setBody(getBodyForUpdate(phoneNumber, oldPassword, newPassword));
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                new SharedPreferenceManager(UpdatePasswordActivity.this)
                        .setStringValue(Constants.KEY_PREF_USER_PASSWORD, newPassword);
                finish();
            }

            @Override
            public void onError(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 400:
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));
                                Toast.makeText(UpdatePasswordActivity.this,
                                        jsonObject.getString(Constants.RESPONSE_KEY_ERROR_MESSAGE),
                                        Toast.LENGTH_SHORT).show();
                            } catch (JSONException je) {
                                je.printStackTrace();
                                Toast.makeText(UpdatePasswordActivity.this, getString(R.string
                                                .error_msg),
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        default:
                            Toast.makeText(UpdatePasswordActivity.this, getString(R.string
                                            .error_msg),
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                error.printStackTrace();
            }
        });
    }

    private JSONObject getBodyForUpdate(String phoneNumber, String oldPassword, String
            newPassword) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER, phoneNumber);
            jsonObject.put(Constants.REQUEST_KEY_CURRENT_PASSWORD, oldPassword);
            jsonObject.put(Constants.REQUEST_KEY_PASSWORD, newPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.LOGD(TAG, jsonObject.toString());
        return jsonObject;
    }
}
