package com.webonise.urbanfarmers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.webonise.urbanfarmers.R;
import com.webonise.urbanfarmers.interfaces.ApiResponseInterface;
import com.webonise.urbanfarmers.models.UserModel;
import com.webonise.urbanfarmers.utilities.Constants;
import com.webonise.urbanfarmers.utilities.LogUtils;
import com.webonise.urbanfarmers.utilities.SharedPreferenceManager;
import com.webonise.urbanfarmers.webservice.WebService;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.etFullName)
    EditText etFullName;
    @Bind(R.id.etPhoneNumber)
    EditText etPhoneNumber;
    @Bind(R.id.etEmailAddress)
    EditText etEmailAddress;
    @Bind(R.id.btnSignUp)
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        setToolbar();

        btnSignUp.setOnClickListener(this);
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            tvTitle.setText(R.string.sign_up);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                validateDataAndSignUp();
                break;
        }
    }

    private void validateDataAndSignUp() {
        String fullName = etFullName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String email = etEmailAddress.getText().toString();
        if (!TextUtils.isEmpty(fullName)) {
            if (!TextUtils.isEmpty(phoneNumber)) {
                registerUser(fullName, phoneNumber, email);
            } else {
                Toast.makeText(SignUpActivity.this, getString(R.string.enter_phone_number), Toast
                        .LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SignUpActivity.this, getString(R.string.enter_full_name), Toast
                    .LENGTH_SHORT).show();
        }
    }

    private void registerUser(String fullName, String phoneNumber, String email) {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.REGISTER_URL);
        webService.setBody(getBody(fullName, phoneNumber, email));
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
                    gotoNextActivity();
                } else {
                    Toast.makeText(SignUpActivity.this, userModel.getMessage(), Toast
                            .LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    private JSONObject getBody(String fullName, String phoneNumber, String email) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_NAME, fullName);
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER, phoneNumber);
            jsonObject.put(Constants.REQUEST_KEY_EMAIl, email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.LOGD(TAG, jsonObject.toString());
        return jsonObject;
    }

    private void gotoNextActivity(){
        Intent intent = new Intent(SignUpActivity.this, CreateGardenActivity.class);
        startActivity(intent);
        finish();
    }
}
