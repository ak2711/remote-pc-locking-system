package com.webonise.gardenIt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.adapters.DashboardRecyclerViewAdapter;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.CreateLogRequestModel;
import com.webonise.gardenIt.models.PlantDetailsModel;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.DateUtil;
import com.webonise.gardenIt.utilities.DisplayUtil;
import com.webonise.gardenIt.utilities.LogUtils;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlantDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    @Bind(R.id.ivPlantImage)
    ImageView ivPlantImage;
    @Bind(R.id.tvDescription)
    TextView tvDescription;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.btnCreateIssue)
    Button btnCreateIssue;
    @Bind(R.id.btnAddLog)
    Button btnAddLog;
    @Bind(R.id.llLogHolder)
    LinearLayout llLogHolder;

    private SharedPreferenceManager sharedPreferenceManager;
    private UserDashboardModel userDashboardModel;
    private PlantDetailsModel plantDetailsModel;
    private DisplayImageOptions options;

    private int position;
    private int gardenId, plantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);
        ButterKnife.bind(this);
        btnAddLog.setOnClickListener(this);
        btnCreateIssue.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager
                    (PlantDetailsActivity.this);
        }
        position = getIntent().getIntExtra(Constants.BUNDLE_KEY_POSITION, 0);
        setToolbar();
        setPlantId();
        getPlantDetails();
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
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
            case R.id.btnCreateIssue:
                goToNextActivity(CreateIssueActivity.class);
                break;
            case R.id.btnAddLog:
                goToNextActivity(CreateLogActivity.class);
                break;
        }
    }

    private void goToNextActivity(Class clazz) {
        Intent intent = new Intent();
        intent.setClass(PlantDetailsActivity.this, clazz);
        if (plantId > 0) {
            intent.putExtra(Constants.BUNDLE_KEY_PLANT_ID, plantId);
        }
        startActivity(intent);
    }

    private void setPlantDetails() {

        final PlantDetailsModel.Plant plant = plantDetailsModel.getPlant();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();

        AppController.getInstance().setupUniversalImageLoader(PlantDetailsActivity.this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                new DisplayUtil(PlantDetailsActivity.this).getImageHeight());
        ivPlantImage.setLayoutParams(layoutParams);
        ImageLoader.getInstance().displayImage(
                Constants.BASE_URL + plant.getImages().get(0).getImage().getUrl(),
                ivPlantImage, options, null);

        tvTitle.setText(plant.getName());
        tvDescription.setText(plant.getDescription());

        plantId = plant.getId();
        gardenId = plant.getGardenId();

        ivPlantImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlantDetailsActivity.this, ActivityImageView.class);
                // Interesting data to pass across are the thumbnail size/location, the
                // resourceId of the source bitmap, the picture description, and the
                // orientation (to avoid returning back to an obsolete configuration if
                // the device rotates again in the meantime)

                int[] screenLocation = new int[2];
                ivPlantImage.getLocationOnScreen(screenLocation);

                //Pass the image title and url to DetailsActivity
                intent.putExtra(Constants.BUNDLE_KEY_LEFT, screenLocation[0]).
                        putExtra(Constants.BUNDLE_KEY_TOP, screenLocation[1]).
                        putExtra(Constants.BUNDLE_KEY_WIDTH, ivPlantImage.getWidth()).
                        putExtra(Constants.BUNDLE_KEY_HEIGHT, ivPlantImage.getHeight()).
                        putExtra(Constants.BUNDLE_KEY_TITLE, plant.getName()).
                        putExtra(Constants.BUNDLE_KEY_IMAGE_URL,
                                Constants.BASE_URL + plant.getImages().get(0).getImage().getUrl());

                //Start details activity
                startActivity(intent);
            }
        });

        List<PlantDetailsModel.Plant.Logs> logsList = plant.getLogs();
        if (logsList.size() > 0) {
            TextView textView = new TextView(this);
            textView.setText(getString(R.string.logs));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextColor(getResources().getColor(R.color.text_color_green));
            textView.setTextSize(new DisplayUtil(PlantDetailsActivity.this).dpToPx(8));
            llLogHolder.addView(textView);

            for (PlantDetailsModel.Plant.Logs logs : logsList) {
                View view = LayoutInflater.from(PlantDetailsActivity.this).inflate(R.layout
                        .log_list_item, null);

                TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                tvTitle.setText(logs.getContent());

                TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
                tvDate.setText(new DateUtil().getFormattedDateFromTimeStamp(logs.getUpdatedAt()));

                ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                if (logs.getImages().size() > 0) {
                    ImageLoader.getInstance().displayImage(
                            Constants.BASE_URL + logs.getImages().get(0).getImage().getUrl(),
                            imageView, options, null);
                }
                llLogHolder.addView(view);
            }
        }
    }

    private void getPlantDetails() {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.GET_PLANT_DETAILS_URL);
        webService.setBody(getBody());
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                plantDetailsModel = new Gson().fromJson(response,
                        PlantDetailsModel.class);
                if (plantDetailsModel.getStatus() == Constants.RESPONSE_CODE_200) {
                    LogUtils.LOGD(TAG, response);
                    setPlantDetails();
                } else {
                    Toast.makeText(PlantDetailsActivity.this, userDashboardModel.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    private JSONObject getBody() {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(PlantDetailsActivity.this);
        }
        UserModel userModel = sharedPreferenceManager.getObject(
                Constants.KEY_PREF_USER, UserModel.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER,
                    userModel.getUser().getPhone_number());
            jsonObject.put(Constants.REQUEST_KEY_PLANT_ID, plantId);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return jsonObject;
    }

    private void setPlantId() {
        userDashboardModel = sharedPreferenceManager.getObject(Constants
                .KEY_PREF_USER_GARDEN_PLANTS, UserDashboardModel.class);

        List<UserDashboardModel.User.Gardens> gardensList = userDashboardModel.getUser()
                .getGardens();
        UserDashboardModel.User.Gardens gardens = gardensList.get(gardensList.size() - 1);
        List<UserDashboardModel.User.Gardens.Plants> plantsList = gardens.getPlants();
        UserDashboardModel.User.Gardens.Plants plants = plantsList.get(position);
        plantId = plants.getId();
    }
}
