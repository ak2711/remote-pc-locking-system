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
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.LogUtils;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    @Bind(R.id.etPhoneNumber)
    EditText etPhoneNumber;
    @Bind(R.id.etPassword)
    EditText etPassword;
    @Bind(R.id.tvNewUser)
    TextView tvNewUser;
    @Bind(R.id.btnSignIn)
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        btnSignIn.setOnClickListener(this);
        tvNewUser.setOnClickListener(this);
        etPassword.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    validateDataAndSignIn();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                validateDataAndSignIn();
                break;

            case R.id.tvNewUser:
                gotoNextActivity(SignUpActivity.class);
                break;
        }
    }

    private void validateDataAndSignIn() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNumber)) {
            if (phoneNumber.length() == 10) {
                if (!TextUtils.isEmpty(password)) {
                    signIn(phoneNumber, password);
                } else {
                    Toast.makeText(SignInActivity.this,
                            getString(R.string.enter_password),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignInActivity.this,
                        getString(R.string.invalid_mobile_number_length),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SignInActivity.this, getString(R.string.enter_phone_number),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn(final String phoneNumber, String password) {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.SIGN_IN_URL);
        webService.setBody(getBody(phoneNumber, password));
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                UserDashboardModel userDashboardModel = new Gson().fromJson(response,
                        UserDashboardModel.class);
                if (userDashboardModel.getStatus() == Constants.RESPONSE_CODE_200) {
                    SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager
                            (SignInActivity.this);
                    sharedPreferenceManager.putObject(Constants.KEY_PREF_USER_GARDEN_PLANTS,
                            userDashboardModel);
                    sharedPreferenceManager.setBooleanValue(Constants.KEY_PREF_IS_USER_LOGGED_IN,
                            true);
                    sharedPreferenceManager.setStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER,
                            userDashboardModel.getUser().getPhoneNumber());
                    if (userDashboardModel.getUser().getGardens() != null
                            && userDashboardModel.getUser().getGardens().size() > 0) {
                        sharedPreferenceManager.setBooleanValue(
                                Constants.KEY_PREF_IS_GARDEN_CREATED,
                                true);
                        gotoNextActivity(DashboardActivity.class);
                    } else {
                        gotoNextActivity(CreateGardenActivity.class);
                    }

                } else {
                    Toast.makeText(SignInActivity.this, userDashboardModel.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 401: //Unauthorized
                            Toast.makeText(SignInActivity.this,
                                    getString(R.string.user_does_not_exists),
                                    Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(SignInActivity.this, getString(R.string.error_msg),
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                }
                error.printStackTrace();
            }
        });
    }

    private JSONObject getBody(String phoneNumber, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER, phoneNumber);
            jsonObject.put(Constants.REQUEST_KEY_PASSWORD, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.LOGD(TAG, jsonObject.toString());
        return jsonObject;
    }

    private void gotoNextActivity(Class clazz) {
        Intent intent = new Intent(SignInActivity.this, clazz);
        startActivity(intent);
        finish();
    }
}
