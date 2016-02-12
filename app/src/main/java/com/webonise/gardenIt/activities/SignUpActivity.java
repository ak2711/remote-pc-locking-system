package com.webonise.gardenIt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.LogUtils;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import android.widget.TextView.OnEditorActionListener;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    @Bind(R.id.etFullName)
    EditText etFullName;
    @Bind(R.id.etPhoneNumber)
    EditText etPhoneNumber;
    @Bind(R.id.etEmailAddress)
    EditText etEmailAddress;
    @Bind(R.id.etPassword)
    EditText etPassword;
    @Bind(R.id.etConfirmPassword)
    EditText etConfirmPassword;
    @Bind(R.id.tvAlreadyAnUser)
    TextView tvAlreadyAnUser;
    @Bind(R.id.etReferredBy)
    EditText etReferredBy;
    @Bind(R.id.btnSignUp)
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        btnSignUp.setOnClickListener(this);
        tvAlreadyAnUser.setOnClickListener(this);
        etReferredBy.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    validateDataAndSignUp();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Obtain the shared Tracker instance.
        AppController application =  AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.ScreenName.SIGN_UP_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                validateDataAndSignUp();
                break;

            case R.id.tvAlreadyAnUser:
                gotoNextActivity(SignInActivity.class);
                break;
        }
    }

    private void validateDataAndSignUp() {
        String fullName = etFullName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String email = etEmailAddress.getText().toString().trim();
        String referredBy = etReferredBy.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = (etConfirmPassword).getText().toString().trim();
        if (!TextUtils.isEmpty(fullName)) {
            if (!TextUtils.isEmpty(phoneNumber)) {
                if (phoneNumber.length() == 10) {
                    if (!TextUtils.isEmpty(password)) {
                        if (password.equals(confirmPassword)) {
                            if (!TextUtils.isEmpty(referredBy) && referredBy.length() == 10) {
                                registerUser(fullName, phoneNumber, email, referredBy, password);
                            } else {
                                Toast.makeText(SignUpActivity.this,
                                        getString(R.string.enter_referral_mobile_number),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this,
                                    getString(R.string.password_does_not_match),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SignUpActivity.this,
                                getString(R.string.enter_password),
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SignUpActivity.this,
                            getString(R.string.invalid_mobile_number_length),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignUpActivity.this, getString(R.string.enter_phone_number),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SignUpActivity.this, getString(R.string.enter_full_name),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(String fullName, final String phoneNumber, String email,
                              String referredBy, final String password) {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.REGISTER_URL);
        webService.setBody(getBody(fullName, phoneNumber, email, referredBy, password));
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                UserModel userModel = new Gson().fromJson(response, UserModel.class);
                if (userModel.getStatus() == Constants.RESPONSE_CODE_200) {
                    SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager
                            (SignUpActivity.this);
                    sharedPreferenceManager.putObject(Constants.KEY_PREF_USER, userModel);
                    sharedPreferenceManager.setBooleanValue(Constants.KEY_PREF_IS_USER_LOGGED_IN,
                            true);
                    sharedPreferenceManager.setStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER,
                            phoneNumber);
                    sharedPreferenceManager.setStringValue(Constants.KEY_PREF_USER_PASSWORD,
                            password);
                    gotoNextActivity(CreateGardenActivity.class);
                } else {
                    Toast.makeText(SignUpActivity.this, userModel.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 400: //Already Exists
                            Toast.makeText(SignUpActivity.this,
                                    getString(R.string.user_already_exists),
                                    Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(SignUpActivity.this, getString(R.string.error_msg),
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                error.printStackTrace();
            }
        });
    }

    private JSONObject getBody(String fullName, String phoneNumber, String email,
                               String referredBy, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_NAME, fullName);
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER, phoneNumber);
            jsonObject.put(Constants.REQUEST_KEY_EMAIl, email);
            jsonObject.put(Constants.REQUEST_KEY_REFERRED_BY, referredBy);
            jsonObject.put(Constants.REQUEST_KEY_PASSWORD, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.LOGD(TAG, jsonObject.toString());
        return jsonObject;
    }

    private void gotoNextActivity(Class clazz) {
        Intent intent = new Intent(SignUpActivity.this, clazz);
        startActivity(intent);
        finish();
    }
}
