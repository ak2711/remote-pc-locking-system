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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.ServiceModel;
import com.webonise.gardenIt.models.ServiceRequestModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.FileContentProvider;
import com.webonise.gardenIt.utilities.ImageUtil;
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

public class RequestServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.etServiceTitle)
    EditText etServiceTitle;
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
    @Bind(R.id.btnRequestService)
    Button btnRequestService;

    private PopupWindow popupWindow;
    private File image_file;
    private SharedPreferenceManager sharedPreferenceManager;
    private int gardenId;
    private ShareUtil shareUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service);
        ButterKnife.bind(this);
        rlCapture.setOnClickListener(this);
        rlGallery.setOnClickListener(this);
        btnRequestService.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gardenId = bundle.getInt(Constants.BUNDLE_KEY_GARDEN_ID);
        }
        setToolbar();
        AppController application =  AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.ScreenName.REQUEST_SERVICE_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tvTitle.setText(R.string.request_a_service);
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
                Intent cameraIntent = new ImageUtil().getCameraIntent();
                startActivityForResult(cameraIntent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.rlGallery:
                Intent galleryIntent = new ImageUtil().getOpenGalleryIntent();
                startActivityForResult(galleryIntent, Constants.PICK_IMAGE);
                break;
            case R.id.btnRequestService:
                validateAndRequestService();
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
        AppController.setupUniversalImageLoader(RequestServiceActivity.this);
        DisplayImageOptions options = ImageUtil.getImageOptions();
        ImageLoader.getInstance().displayImage("file://" + image_file.toString(), ivToUpload,
                options);
        ivCancel.setVisibility(View.VISIBLE);
    }

    private void validateAndRequestService() {
        String title, description;
        title = etServiceTitle.getText().toString();
        description = etDescription.getText().toString();

        if (!TextUtils.isEmpty(title)) {
            if (!TextUtils.isEmpty(description)) {
                requestService(title, description);
            } else {
                Toast.makeText(RequestServiceActivity.this, getString(R.string.enter_description),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(RequestServiceActivity.this, getString(R.string.provide_title), Toast
                    .LENGTH_LONG).show();
        }
    }

    private void requestService(String title, String description) {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.REQUEST_SERVICE_URL);
        webService.setBody(getBody(title, description));
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                ServiceModel serviceModel = new Gson().fromJson(response,
                        ServiceModel.class);
                if (serviceModel.getStatus() == Constants.RESPONSE_CODE_200) {
                    showSuccessPopUp();
                    Thread sleeper = new Thread(sleepRunnable);
                    sleeper.start();
                } else {
                    Toast.makeText(RequestServiceActivity.this, serviceModel.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(RequestServiceActivity.this, getString(R.string.error_msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private JSONObject getBody(String title, String description) {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(RequestServiceActivity.this);
        }
        String phoneNumber = sharedPreferenceManager
                .getStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER);
        ServiceRequestModel serviceRequestModel = new ServiceRequestModel();

        try {
            serviceRequestModel.setTitle(title);
            serviceRequestModel.setDescription(description);
            serviceRequestModel.setPhoneNumber(phoneNumber);

            if (gardenId > 0) {
                serviceRequestModel.setGardenId(gardenId);
            }
            List<ServiceRequestModel.PlantImage> plantImages = new ArrayList<>();

            ServiceRequestModel.PlantImage plantImage = serviceRequestModel.new
                    PlantImage();
            plantImage.setImage(Constants.REQUEST_ADDITIONAL_PARAMETER_FOR_IMAGE
                    + getEncodedImage());

            plantImages.add(plantImage);

            serviceRequestModel.setPlantImage(plantImages);

            return new JSONObject(new Gson().toJson(serviceRequestModel));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getEncodedImage() {
        if (image_file != null) {
            Bitmap bitmap = ((BitmapDrawable) ivToUpload.getDrawable()).getBitmap();
            return ImageUtil.encodeTobase64(bitmap);
        } else {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareUtil == null) {
            shareUtil = new ShareUtil(RequestServiceActivity.this);
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
        imageView.setImageResource(R.drawable.success_service_request_icon);

        TextView textView1 = (TextView) popupView.findViewById(R.id.textView1);
        textView1.setText(R.string.success_service);

        TextView textView2 = (TextView) popupView.findViewById(R.id.textView2);
        textView2.setText(R.string.success_service_desc);

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
                        finish();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
