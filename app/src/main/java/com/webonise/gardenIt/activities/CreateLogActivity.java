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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.CreateGardenModel;
import com.webonise.gardenIt.models.CreateIssueRequestModel;
import com.webonise.gardenIt.models.CreateLogRequestModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.FileContentProvider;
import com.webonise.gardenIt.utilities.ImageUtil;
import com.webonise.gardenIt.utilities.PermissionsUtil;
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

public class CreateLogActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.etLogTitle)
    EditText etLogTitle;
    @Bind(R.id.ivToUpload)
    ImageView ivToUpload;
    @Bind(R.id.rlCapture)
    RelativeLayout rlCapture;
    @Bind(R.id.rlGallery)
    RelativeLayout rlGallery;
    @Bind(R.id.btnAddLog)
    Button btnAddLog;
    @Bind(R.id.ivCancel)
    ImageView ivCancel;

    private File image_file;
    private SharedPreferenceManager sharedPreferenceManager;
    private int plantId;
    private ShareUtil shareUtil;
    private int gardenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_create_log);
        ButterKnife.bind(this);
        rlCapture.setOnClickListener(this);
        rlGallery.setOnClickListener(this);
        btnAddLog.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            plantId = bundle.getInt(Constants.BUNDLE_KEY_PLANT_ID);
            gardenId = bundle.getInt(Constants.BUNDLE_KEY_GARDEN_ID);
        }
        setToolbar();
        AppController application = AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.ScreenName.ADD_LOGS_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tvTitle.setText(getString(R.string.add_log));
            toolbar.setNavigationIcon(R.drawable.ic_action_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlCapture:
                if (PermissionsUtil.checkPermissionForCamera(this)) {
                    startCameraIntent();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                            .CAMERA}, PermissionsUtil.CAMERA_REQUEST_CODE);
                }

                break;
            case R.id.rlGallery:
                if (PermissionsUtil.checkPermissionForExternalStorage(this)) {
                    startGalleryIntent();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                            .WRITE_EXTERNAL_STORAGE}, PermissionsUtil
                            .EXTERNAL_STORAGE_REQUEST_CODE);
                }
                break;
            case R.id.btnAddLog:
                validateAndCreateLog();
                break;
            case R.id.ivCancel:
                image_file = null;
                ivToUpload.setImageDrawable(null);
                ivCancel.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                    ImageUtil.deleteCapturedPhoto();
                image_file = new File(getFilesDir(), FileContentProvider.getUniqueFileName());
                showImage();
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
                    showImage();
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_pic_not_taken), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    protected void showImage() {
        AppController.setupUniversalImageLoader(CreateLogActivity.this);
        DisplayImageOptions options = ImageUtil.getImageOptions();
        ImageLoader.getInstance().displayImage("file://" + image_file.toString(), ivToUpload,
                options);
        ivCancel.setVisibility(View.VISIBLE);
    }

    private void validateAndCreateLog() {
        String title;
        title = etLogTitle.getText().toString();

        if (!TextUtils.isEmpty(title)) {
            createLog(title);
        } else {
            Toast.makeText(CreateLogActivity.this, getString(R.string.provide_title),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void createLog(String title) {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.ADD_LOG_URL);
        webService.setBody(getBody(title));
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                CreateGardenModel createGardenModel = new Gson().fromJson(response,
                        CreateGardenModel.class);
                if (createGardenModel.getStatus() == Constants.RESPONSE_CODE_200) {
                    finish();
                } else {
                    Toast.makeText(CreateLogActivity.this, createGardenModel.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(CreateLogActivity.this, getString(R.string.error_msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private JSONObject getBody(String title) {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(CreateLogActivity.this);
        }
        String phoneNumber = sharedPreferenceManager
                .getStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER);

        CreateLogRequestModel createLogRequestModel = new CreateLogRequestModel();

        try {
            createLogRequestModel.setContent(title);
            createLogRequestModel.setPhoneNumber(phoneNumber);

            if (plantId > 0) {
                createLogRequestModel.setPlantId(plantId);
            }
            if (gardenId > 0) {
                createLogRequestModel.setGardenId(gardenId);
            }

            if (image_file != null) {
                List<CreateLogRequestModel.PlantImage> plantImages = new ArrayList<>();

                CreateLogRequestModel.PlantImage plantImage = createLogRequestModel.new
                        PlantImage();
                plantImage.setImage(Constants.REQUEST_ADDITIONAL_PARAMETER_FOR_IMAGE
                        + getEncodedImage());

                plantImages.add(plantImage);

                createLogRequestModel.setPlantImage(plantImages);
            }

            return new JSONObject(new Gson().toJson(createLogRequestModel));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getEncodedImage() {
        Bitmap bitmap = ((BitmapDrawable) ivToUpload.getDrawable()).getBitmap();
        return ImageUtil.encodeTobase64(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareUtil == null) {
            shareUtil = new ShareUtil(CreateLogActivity.this);
        }
        shareUtil.deleteImageFile();
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
