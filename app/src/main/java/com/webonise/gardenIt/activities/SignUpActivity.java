package com.webonise.gardenIt.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.webonise.gardenIt.utilities.PermissionsUtil;
import com.webonise.gardenIt.utilities.ShareUtil;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.utilities.UriManager;
import com.webonise.gardenIt.webservice.WebService;

import android.widget.TextView.OnEditorActionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

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
    @Bind(R.id.ivProfilePic)
    ImageView ivProfilePic;


    private PopupWindow popupWindow;
    private File image_file;
    private ShareUtil shareUtil;
    private boolean isEditable;
    private boolean isNewImage = true;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        getBundleData();
        if (isEditable) {
            setupData();
        }
        setToolbar();
        btnSignUp.setOnClickListener(this);
        ivProfilePic.setOnClickListener(this);
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

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isEditable = bundle.getBoolean(Constants.BUNDLE_KEY_IS_EDITABLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Obtain the shared Tracker instance.
        AppController application = AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(isEditable ? Constants.ScreenName.UPDATE_USER_DETAILS_SCREEN
                : Constants.ScreenName.SIGN_UP_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            if (isEditable) {
                toolbar.setNavigationIcon(R.drawable.ic_action_back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
                tvTitle.setText(R.string.my_profile);
            } else {
                tvTitle.setText(R.string.sign_up);
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        switch (v.getId()) {

            case R.id.ivProfilePic:
                showPopupWindow();
                break;
            case R.id.btnSignUp:
                if (isEditable) {
                    validateAndUpdate();
                } else {
                    validateDataAndSignUp();
                }
                break;

            case R.id.tvAlreadyAnUser:
                gotoNextActivity(SignInActivity.class);
                break;

            case R.id.btnOpenCamera:
                if (PermissionsUtil.checkPermissionForCamera(this)) {
                    startCameraIntent();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                            .CAMERA}, PermissionsUtil.CAMERA_REQUEST_CODE);
                }
                break;
            case R.id.btnOpenGallery:
                if (PermissionsUtil.checkPermissionForExternalStorage(this)) {
                    startGalleryIntent();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                            .WRITE_EXTERNAL_STORAGE}, PermissionsUtil
                            .EXTERNAL_STORAGE_REQUEST_CODE);
                }
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
                        case 400: //Already Exists, Invalid Referral
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));
                                Toast.makeText(SignUpActivity.this,
                                        jsonObject.getString(Constants.RESPONSE_KEY_ERROR_MESSAGE),
                                        Toast.LENGTH_SHORT).show();
                            } catch (JSONException je) {
                                je.printStackTrace();
                                Toast.makeText(SignUpActivity.this, getString(R.string.error_msg),
                                        Toast.LENGTH_LONG).show();
                            }
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

        SignUpRequestModel signUpRequestModel = new SignUpRequestModel();
        JSONObject jsonObject = new JSONObject();
        try {
            signUpRequestModel.setName(fullName);
            signUpRequestModel.setEmail(email);
            signUpRequestModel.setPassword(password);
            signUpRequestModel.setPhoneNumber(phoneNumber);
            signUpRequestModel.setReferredBy(referredBy);
            if (isNewImage) {
                List<SignUpRequestModel.PlantImage> plantImages = new ArrayList<>();
                SignUpRequestModel.PlantImage plantImage = signUpRequestModel.new PlantImage();
                plantImage.setImage(Constants.REQUEST_ADDITIONAL_PARAMETER_FOR_IMAGE
                        + getEncodedImage());

                plantImages.add(plantImage);
                signUpRequestModel.setPlantImage(plantImages);
            }
            jsonObject = new JSONObject(new Gson().toJson(signUpRequestModel));
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

    private void showPopupWindow() {

        LayoutInflater layoutInflater
                = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.add_photo_pop_up_menu, (ViewGroup)
                findViewById(R.id.popupWindow));

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button btnOpenCamera = (Button) popupView.findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(this);

        Button btnOpenGallery = (Button) popupView.findViewById(R.id.btnOpenGallery);
        btnOpenGallery.setOnClickListener(this);

        ImageButton btnCancel = (ImageButton) popupView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                    ImageUtil.deleteCapturedPhoto();
                image_file = new File(getFilesDir(), FileContentProvider.getUniqueFileName());
                showImage("file://" + image_file.toString());
            }
        } else if (requestCode == Constants.PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                String realPath = UriManager.getPath(uri, this);
                if (TextUtils.isEmpty(realPath)) {
                    Toast.makeText(this, getString(R.string.toast_online_image), Toast
                            .LENGTH_LONG).show();
                } else {
                    image_file = new File(realPath);
                    showImage("file://" + image_file.toString());
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_pic_not_taken), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    protected void showImage(String filepath) {
        AppController.setupUniversalImageLoader(SignUpActivity.this);
        DisplayImageOptions options = ImageUtil.getImageOptions();
        ImageLoader.getInstance().displayImage(filepath, ivProfilePic,
                options);
        isNewImage = true;
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareUtil == null) {
            shareUtil = new ShareUtil(SignUpActivity.this);
        }
        shareUtil.deleteImageFile();
    }

    private String getEncodedImage() {
        Bitmap bitmap = ((BitmapDrawable) ivProfilePic.getDrawable()).getBitmap();
        return ImageUtil.encodeTobase64(bitmap);
    }

    private void setupData() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        UserDashboardModel userDashboardModel = sharedPreferenceManager.getObject(Constants
                .KEY_PREF_USER_GARDEN_PLANTS, UserDashboardModel.class);
        UserDashboardModel.User user = userDashboardModel.getUser();
        if (user != null) {
            etFullName.setText(user.getName());
            etEmailAddress.setText(user.getEmail());
            etPhoneNumber.setText(user.getPhoneNumber());
            etPassword.setVisibility(View.GONE);
            etConfirmPassword.setVisibility(View.GONE);
            etReferredBy.setVisibility(View.GONE);
            btnSignUp.setText(getString(R.string.update));
            tvAlreadyAnUser.setVisibility(View.GONE);

            userId = user.getId();
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.icon_profile_pic)
                    .showImageForEmptyUri(R.drawable.icon_profile_pic)
                    .showImageOnFail(R.drawable.icon_profile_pic)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(500))
                    .build();

            AppController.getInstance().setupUniversalImageLoader(this);
            ImageLoader.getInstance().displayImage(
                    Constants.BASE_URL + user.getProfileImage(),
                    ivProfilePic, options, null);
        }
    }

    private void validateAndUpdate() {
        String fullName = etFullName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String email = etEmailAddress.getText().toString().trim();
        if (!TextUtils.isEmpty(fullName)) {
            if (!TextUtils.isEmpty(phoneNumber)) {
                if (phoneNumber.length() == 10) {
                    updateUser(fullName, phoneNumber, email);
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

    private void updateUser(String fullName, final String phoneNumber, String email) {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.EDIT_USER_URL);
        webService.setBody(getBodyForUpdate(fullName, phoneNumber, email));
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
                    Intent intent = new Intent(SignUpActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
                        case 400: //Already Exists, Invalid Referral
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));
                                Toast.makeText(SignUpActivity.this,
                                        jsonObject.getString(Constants.RESPONSE_KEY_ERROR_MESSAGE),
                                        Toast.LENGTH_SHORT).show();
                            } catch (JSONException je) {
                                je.printStackTrace();
                                Toast.makeText(SignUpActivity.this, getString(R.string.error_msg),
                                        Toast.LENGTH_LONG).show();
                            }
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

    private JSONObject getBodyForUpdate(String fullName, String phoneNumber, String email) {

        UserDetailsUpdateRequestModel userDetailsUpdateRequestModel
                = new UserDetailsUpdateRequestModel();
        JSONObject jsonObject = new JSONObject();
        try {
            userDetailsUpdateRequestModel.setName(fullName);
            userDetailsUpdateRequestModel.setEmail(email);
            userDetailsUpdateRequestModel.setPhoneNumber(phoneNumber);
            userDetailsUpdateRequestModel.setUserId(userId);
            if (isNewImage) {
                List<UserDetailsUpdateRequestModel.PlantImage> plantImages = new ArrayList<>();
                UserDetailsUpdateRequestModel.PlantImage plantImage =
                        userDetailsUpdateRequestModel.new PlantImage();
                plantImage.setImage(Constants.REQUEST_ADDITIONAL_PARAMETER_FOR_IMAGE
                        + getEncodedImage());

                plantImages.add(plantImage);
                userDetailsUpdateRequestModel.setPlantImage(plantImages);
            }
            jsonObject = new JSONObject(new Gson().toJson(userDetailsUpdateRequestModel));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.LOGD(TAG, jsonObject.toString());

        return jsonObject;
    }

    private void startCameraIntent() {
        Intent cameraIntent = ImageUtil.getCameraIntent();
        startActivityForResult(cameraIntent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void startGalleryIntent() {
        Intent galleryIntent = ImageUtil.getOpenGalleryIntent();
        startActivityForResult(galleryIntent, Constants.PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionsUtil.CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    startCameraIntent();
                }
                break;
            case PermissionsUtil.EXTERNAL_STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    startGalleryIntent();
                }

                break;
        }
    }
}
