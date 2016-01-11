package com.webonise.gardenIt.activities;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.DisplayUtil;
import com.webonise.gardenIt.utilities.LogUtils;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlantDescriptionActivity extends AppCompatActivity implements View.OnClickListener {

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
    Button btnRequestService;
    @Bind(R.id.btnRequestService)
    Button btnCreateIssue;

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
        btnRequestService.setOnClickListener(this);
        btnCreateIssue.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager
                    (PlantDescriptionActivity.this);
        }
        position = getIntent().getIntExtra(Constants.BUNDLE_KEY_POSITION,0);
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
                goToCreateIssueActivity();
                break;
            case R.id.btnRequestService:
                goToServiceRequestActivity();
                break;
        }
    }

    private void goToCreateIssueActivity() {
        Intent intent = new Intent();
        intent.setClass(PlantDescriptionActivity.this, CreateIssueActivity.class);
        startActivity(intent);
    }

    private void goToServiceRequestActivity() {
        Intent intent = new Intent();
        intent.setClass(PlantDescriptionActivity.this, RequestServiceActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_GARDEN_ID, gardenId);
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

        AppController.getInstance().setupUniversalImageLoader(PlantDescriptionActivity.this);

        UserDashboardModel.User.Gardens.Plants plants = plantsList.get(position);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                new DisplayUtil(PlantDescriptionActivity.this).getImageHeight());
        ivPlantImage.setLayoutParams(params);
        ImageLoader.getInstance().displayImage(
                Constants.BASE_URL + plants.getImages().get(0).getImage().getUrl(),
                ivPlantImage, options, null);

        tvTitle.setText(plants.getName());
        tvDescription.setText(plants.getDescription());

        plantId = plants.getId();
        gardenId = plants.getGardenId();
    }
}
