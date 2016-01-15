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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.AddPlantRequestModel;
import com.webonise.gardenIt.models.CreateGardenModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.FileContentProvider;
import com.webonise.gardenIt.utilities.ImageUtil;
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
    @Bind(R.id.rlCapture)
    RelativeLayout rlCapture;
    @Bind(R.id.rlGallery)
    RelativeLayout rlGallery;
    @Bind(R.id.btnAddPlant)
    Button btnAddPlant;

    private File image_file;
    private SharedPreferenceManager sharedPreferenceManager;
    private boolean showBackButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        ButterKnife.bind(this);
        rlCapture.setOnClickListener(this);
        rlGallery.setOnClickListener(this);
        btnAddPlant.setOnClickListener(this);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            showBackButton = bundle.getBoolean(Constants.BUNDLE_KEY_SHOW_BACK_ICON);
        }
        setToolbar();
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
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
                Intent cameraIntent = new ImageUtil().getCameraIntent();
                startActivityForResult(cameraIntent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.rlGallery:
                Intent galleryIntent = new ImageUtil().getOpenGalleryIntent();
                startActivityForResult(galleryIntent, Constants.PICK_IMAGE);
                break;
            case R.id.btnAddPlant:
                validateAndAddPlant();
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
        AppController.setupUniversalImageLoader(AddPlantActivity.this);
        DisplayImageOptions options = ImageUtil.getImageOptions();
        ImageLoader.getInstance().displayImage("file://" + image_file.toString(), ivToUpload,
                options);
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
        webService.setUrl(Constants.ADD_PLANT_URL);
        webService.setBody(getBody(nameOfPlant, description));
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                CreateGardenModel createGardenModel = new Gson().fromJson(response,
                        CreateGardenModel.class);
                if (createGardenModel.getStatus() == Constants.RESPONSE_CODE_200) {
                    if (sharedPreferenceManager == null) {
                        sharedPreferenceManager = new SharedPreferenceManager
                                (AddPlantActivity.this);
                    }
                    sharedPreferenceManager.setBooleanValue(Constants.KEY_PREF_IS_PLANT_ADDED,
                            true);
                    finish();
                } else {
                    Toast.makeText(AddPlantActivity.this, createGardenModel.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    private JSONObject getBody(String nameOfPlant, String description) {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(AddPlantActivity.this);
        }
        UserModel userModel = sharedPreferenceManager.getObject(
                Constants.KEY_PREF_USER, UserModel.class);

        CreateGardenModel createGardenModel = sharedPreferenceManager.getObject(Constants
                .KEY_PREF_GARDEN_DETAILS, CreateGardenModel.class);
        AddPlantRequestModel addPlantRequestModel = new AddPlantRequestModel();

        try {
            addPlantRequestModel.setName(nameOfPlant);
            addPlantRequestModel.setDescription(description);
            addPlantRequestModel.setPhoneNumber(userModel.getUser().getPhone_number());
            addPlantRequestModel.setGardenId(createGardenModel.getGarden().getId());

            List<AddPlantRequestModel.PlantImage> plantImages = new ArrayList<>();

            AddPlantRequestModel.PlantImage plantImage = addPlantRequestModel.new PlantImage();
            plantImage.setImage(Constants.REQUEST_ADDITIONAL_PARAMETER_FOR_IMAGE
                    + getEncodedImage());

            plantImages.add(plantImage);

            addPlantRequestModel.setPlantImage(plantImages);

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
}
