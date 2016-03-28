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
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.webonise.gardenIt.models.AddPlantModel;
import com.webonise.gardenIt.models.AddPlantRequestModel;
import com.webonise.gardenIt.models.CreateGardenModel;
import com.webonise.gardenIt.models.UserDashboardModel;
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

public class AddPlantActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.etNameOfPlant)
    EditText etNameOfPlant;
    @Bind(R.id.etDescription)
    EditText etDescription;
    @Bind(R.id.ivToUpload)
    ImageView ivToUpload;
    @Bind(R.id.ivCancel)
    ImageView ivCancel;
    @Bind(R.id.rlCapture)
    RelativeLayout rlCapture;
    @Bind(R.id.rlGallery)
    RelativeLayout rlGallery;
    @Bind(R.id.btnAddPlant)
    Button btnAddPlant;

    private File image_file;
    private SharedPreferenceManager sharedPreferenceManager;
    private boolean showBackButton = false;
    private ShareUtil shareUtil;
    private PopupWindow popupWindow;

    private int plantId, gardenId;
    private String plantName, description, plantImageUrl;
    private boolean isNewImage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_add_plant);
        ButterKnife.bind(this);
        rlCapture.setOnClickListener(this);
        rlGallery.setOnClickListener(this);
        btnAddPlant.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        etDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    validateAndAddPlant();
                    return true;
                }
                return false;
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gardenId = bundle.getInt(Constants.BUNDLE_KEY_GARDEN_ID);
            showBackButton = bundle.getBoolean(Constants.BUNDLE_KEY_SHOW_BACK_ICON);
            plantId = bundle.getInt(Constants.BUNDLE_KEY_PLANT_ID);
        }
        if (plantId > 0) {
            setData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setToolbar();
        AppController application = AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.ScreenName.ADD_PLANT_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tvTitle.setText(R.string.add_new_plant);
            if (showBackButton) {
                toolbar.setNavigationIcon(R.drawable.ic_action_back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
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
            case R.id.btnAddPlant:
                validateAndAddPlant();
                break;
            case R.id.ivCancel:
                image_file = null;
                ivToUpload.setImageDrawable(null);
                ivCancel.setVisibility(View.GONE);
                //User has removed the selected/captured image
                isNewImage = false;
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
                showImage("file://" + image_file.toString());
                //User has captured new image
                isNewImage = true;
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
                    //User has selected new image
                    isNewImage = true;
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_pic_not_taken), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    protected void showImage(String filepath) {
        AppController.setupUniversalImageLoader(AddPlantActivity.this);
        DisplayImageOptions options = ImageUtil.getImageOptions();
        ImageLoader.getInstance().displayImage(filepath, ivToUpload,
                options);
        ivCancel.setVisibility(View.VISIBLE);
    }

    private void validateAndAddPlant() {
        String nameOfPlant, description;
        nameOfPlant = etNameOfPlant.getText().toString();
        description = etDescription.getText().toString();

        if (!TextUtils.isEmpty(nameOfPlant)) {
            if (!TextUtils.isEmpty(description)) {
                if (image_file != null) {
                    addPlant(nameOfPlant, description);
                } else {
                    Toast.makeText(AddPlantActivity.this, getString(R.string.provide_image),
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(AddPlantActivity.this, getString(R.string.enter_description),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(AddPlantActivity.this, getString(R.string.enter_plant_name), Toast
                    .LENGTH_LONG).show();
        }
    }

    private void addPlant(String nameOfPlant, String description) {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(plantId > 0 ? Constants.EDIT_PLANT_URL : Constants.ADD_PLANT_URL);
        webService.setBody(getBody(nameOfPlant, description));
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {

                showSuccessPopUp();
                Thread sleeper = new Thread(sleepRunnable);
                sleeper.start();

                AddPlantModel addPlantModel = new Gson().fromJson(response,
                        AddPlantModel.class);
                if (addPlantModel.getStatus() == Constants.RESPONSE_CODE_200) {
                    if (sharedPreferenceManager == null) {
                        sharedPreferenceManager = new SharedPreferenceManager
                                (AddPlantActivity.this);
                    }
                    sharedPreferenceManager.setBooleanValue(Constants.KEY_PREF_IS_PLANT_ADDED,
                            true);
                } else {
                    Toast.makeText(AddPlantActivity.this, addPlantModel.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(AddPlantActivity.this, getString(R.string.error_msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private JSONObject getBody(String nameOfPlant, String description) {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(AddPlantActivity.this);
        }

        AddPlantRequestModel addPlantRequestModel = new AddPlantRequestModel();

        try {
            addPlantRequestModel.setName(nameOfPlant);
            addPlantRequestModel.setDescription(description);
            addPlantRequestModel.setPhoneNumber(sharedPreferenceManager
                    .getStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER));
            addPlantRequestModel.setGardenId(gardenId > 0 ? gardenId : sharedPreferenceManager
                    .getIntValue(Constants.KEY_PREF_GARDEN_ID));
            if (plantId > 0) {
                addPlantRequestModel.setPlantId(plantId);
            }

            /**
             * If user has selected new image then only we need to send this data to server.
             * In case of edit plant, isNewImage can be false and we might not need to send the
             * image to server.
             * isNewImage is true in case of User captures or select and image from gallery.
             * The isNewImage value is false when user edits the plant or cancels the image.
             *
             */
            if (isNewImage) {
                List<AddPlantRequestModel.PlantImage> plantImages = new ArrayList<>();

                AddPlantRequestModel.PlantImage plantImage = addPlantRequestModel.new PlantImage();
                plantImage.setImage(Constants.REQUEST_ADDITIONAL_PARAMETER_FOR_IMAGE
                        + getEncodedImage());

                plantImages.add(plantImage);

                addPlantRequestModel.setPlantImage(plantImages);
            }

            return new JSONObject(new Gson().toJson(addPlantRequestModel));

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
            shareUtil = new ShareUtil(AddPlantActivity.this);
        }
        shareUtil.deleteImageFile();
    }

    private void showSuccessPopUp() {
        LayoutInflater layoutInflater
                = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.success_view,
                (ViewGroup) findViewById(R.id.popupWindow));

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        ImageView imageView = (ImageView) popupView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.add_plant_success_icon);

        TextView textView1 = (TextView) popupView.findViewById(R.id.textView1);
        textView1.setText(R.string.success_plant);

        TextView textView2 = (TextView) popupView.findViewById(R.id.textView2);
        textView2.setText(R.string.success_plant_desc);

    }

    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    public Runnable sleepRunnable = new Runnable() {
        public void run() {
            try {
                Thread.sleep(Constants.SUCCESS_STATE_VISIBLE_TIME);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissPopupWindow();
                        Intent intent = new Intent(AddPlantActivity.this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void setData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            plantName = bundle.getString(Constants.BUNDLE_KEY_TITLE);
            description = bundle.getString(Constants.BUNDLE_KEY_DESC);
            plantImageUrl = bundle.getString(Constants.BUNDLE_KEY_IMAGE_URL);

            etNameOfPlant.setText(plantName);
            etDescription.setText(description);
            showImage(plantImageUrl);
            btnAddPlant.setText(getString(R.string.update_plant));
            image_file = new File(plantImageUrl);
            isNewImage = false;
        }
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
