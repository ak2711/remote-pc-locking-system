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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.webonise.gardenIt.models.CreateGardenModel;
import com.webonise.gardenIt.models.CreateIssueRequestModel;
import com.webonise.gardenIt.models.UserModel;
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

public class CreateIssueActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.etIssueTitle)
    EditText etIssueTitle;
    @Bind(R.id.etDescription)
    EditText etDescription;
    @Bind(R.id.ivToUpload)
    ImageView ivToUpload;
    @Bind(R.id.ivShare)
    ImageView ivShare;
    @Bind(R.id.rlCapture)
    RelativeLayout rlCapture;
    @Bind(R.id.rlGallery)
    RelativeLayout rlGallery;
    @Bind(R.id.btnCreateIssue)
    Button btnCreateIssue;

    private File image_file;
    private SharedPreferenceManager sharedPreferenceManager;
    private int plantId;
    private ShareUtil shareUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_issue);
        ButterKnife.bind(this);
        rlCapture.setOnClickListener(this);
        rlGallery.setOnClickListener(this);
        btnCreateIssue.setOnClickListener(this);
        ivShare.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            plantId = bundle.getInt(Constants.BUNDLE_KEY_PLANT_ID);
        }
        setToolbar();
        AppController application =  AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.ScreenName.GET_ADVICE_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            tvTitle.setText(getString(R.string.create_an_issue));
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
            case R.id.btnCreateIssue:
                validateAndCreateIssue();
                break;
            case R.id.ivShare:
                shareUtil = new ShareUtil(this);
                shareUtil.shareContent(shareUtil.getLocalBitmapUri(ivToUpload));
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
        AppController.setupUniversalImageLoader(CreateIssueActivity.this);
        DisplayImageOptions options = ImageUtil.getImageOptions();
        ImageLoader.getInstance().displayImage("file://" + image_file.toString(), ivToUpload,
                options);
        ivShare.setVisibility(View.VISIBLE);
    }

    private void validateAndCreateIssue() {
        String title, description;
        title = etIssueTitle.getText().toString();
        description = etDescription.getText().toString();

        if (!TextUtils.isEmpty(title)) {
            if (!TextUtils.isEmpty(description)) {
                if (image_file != null) {
                    createIssue(title, description);
                } else {
                    Toast.makeText(CreateIssueActivity.this, getString(R.string.provide_image),
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(CreateIssueActivity.this, getString(R.string.enter_description),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(CreateIssueActivity.this, getString(R.string.provide_title), Toast
                    .LENGTH_LONG).show();
        }
    }

    private void createIssue(String title, String description) {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.CREATE_ISSUE_URL);
        webService.setBody(getBody(title, description));
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                CreateGardenModel createGardenModel = new Gson().fromJson(response,
                        CreateGardenModel.class);
                if (createGardenModel.getStatus() == Constants.RESPONSE_CODE_200) {
                    finish();
                } else {
                    Toast.makeText(CreateIssueActivity.this, createGardenModel.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(CreateIssueActivity.this, getString(R.string.error_msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private JSONObject getBody(String nameOfPlant, String description) {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(CreateIssueActivity.this);
        }
        String phoneNumber = sharedPreferenceManager
                .getStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER);

        CreateIssueRequestModel createIssueRequestModel = new CreateIssueRequestModel();

        try {
            createIssueRequestModel.setName(nameOfPlant);
            createIssueRequestModel.setDescription(description);
            createIssueRequestModel.setPhoneNumber(phoneNumber);

            if (plantId > 0) {
                createIssueRequestModel.setPlantId(plantId);
            }
            List<CreateIssueRequestModel.PlantImage> plantImages = new ArrayList<>();

            CreateIssueRequestModel.PlantImage plantImage = createIssueRequestModel.new
                    PlantImage();
            plantImage.setImage(Constants.REQUEST_ADDITIONAL_PARAMETER_FOR_IMAGE
                    + getEncodedImage());

            plantImages.add(plantImage);

            createIssueRequestModel.setPlantImage(plantImages);

            return new JSONObject(new Gson().toJson(createIssueRequestModel));

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
        if (shareUtil != null) {
            shareUtil.deleteImageFile();
        }
    }
}
