package com.webonise.gardenIt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.DisplayUtil;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;

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

    private SharedPreferenceManager sharedPreferenceManager;
    private UserDashboardModel userDashboardModel;
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
        setPlantDetails();
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
                goToCreateIssueActivity(getString(R.string.create_an_issue));
                break;
            case R.id.btnAddLog:
                goToCreateIssueActivity(getString(R.string.add_log));
                break;
        }
    }

    private void goToCreateIssueActivity(String title) {
        Intent intent = new Intent();
        intent.setClass(PlantDetailsActivity.this, CreateIssueActivity.class);
        if (plantId > 0) {
            intent.putExtra(Constants.BUNDLE_KEY_PLANT_ID, plantId);
        }
        intent.putExtra(Constants.BUNDLE_KEY_TITLE, title);
        startActivity(intent);
    }

    private void setPlantDetails() {
        userDashboardModel = sharedPreferenceManager.getObject(Constants
                .KEY_PREF_USER_GARDEN_PLANTS, UserDashboardModel.class);

        List<UserDashboardModel.User.Gardens> gardensList = userDashboardModel.getUser()
                .getGardens();
        UserDashboardModel.User.Gardens gardens = gardensList.get(gardensList.size() - 1);
        List<UserDashboardModel.User.Gardens.Plants> plantsList = gardens.getPlants();
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

        final UserDashboardModel.User.Gardens.Plants plants = plantsList.get(position);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                new DisplayUtil(PlantDetailsActivity.this).getImageHeight());
        ivPlantImage.setLayoutParams(layoutParams);
        ImageLoader.getInstance().displayImage(
                Constants.BASE_URL + plants.getImages().get(0).getImage().getUrl(),
                ivPlantImage, options, null);

        tvTitle.setText(plants.getName());
        tvDescription.setText(plants.getDescription());

        plantId = plants.getId();
        gardenId = plants.getGardenId();

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
                        putExtra(Constants.BUNDLE_KEY_TITLE, plants.getName()).
                        putExtra(Constants.BUNDLE_KEY_IMAGE_URL,
                                Constants.BASE_URL + plants.getImages().get(0).getImage().getUrl());

                //Start details activity
                startActivity(intent);
            }
        });
    }
}
