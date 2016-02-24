package com.webonise.gardenIt.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.AddPlantModel;
import com.webonise.gardenIt.models.AddPlantRequestModel;
import com.webonise.gardenIt.models.PlantDetailsModel;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.ColorUtil;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.DateUtil;
import com.webonise.gardenIt.utilities.DisplayUtil;
import com.webonise.gardenIt.utilities.LogUtils;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

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
    @Bind(R.id.tvDate)
    TextView tvDate;

    private SharedPreferenceManager sharedPreferenceManager;
    private UserDashboardModel userDashboardModel;
    private PlantDetailsModel plantDetailsModel;
    private DisplayImageOptions options;
    private String plantName, description, plantImageUrl;
    private int gardenId, plantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
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
        plantId = getIntent().getIntExtra(Constants.BUNDLE_KEY_PLANT_ID, 0);
        setToolbar();
        //setPlantId();
        getPlantDetails();
        AppController application = AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.ScreenName.PLANT_DETAILS_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plant_details_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit:
                Intent intent = new Intent(PlantDetailsActivity.this, AddPlantActivity.class);
                intent.putExtra(Constants.BUNDLE_KEY_PLANT_ID, plantId);
                intent.putExtra(Constants.BUNDLE_KEY_SHOW_BACK_ICON, true);
                intent.putExtra(Constants.BUNDLE_KEY_TITLE, plantName);
                intent.putExtra(Constants.BUNDLE_KEY_DESC, description);
                intent.putExtra(Constants.BUNDLE_KEY_IMAGE_URL, plantImageUrl);

                startActivity(intent);
                break;

            case R.id.action_delete:
                buildAlertDialogForDelete();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        finish();
    }

    private List<Object> sortLogsAndIssues() {
        List<Object> sortedList = new ArrayList<>();

        PlantDetailsModel.Plant plant = plantDetailsModel.getPlant();
        List<PlantDetailsModel.Plant.Logs> logsList = plant.getLogs();
        if (logsList != null && logsList.size() > 0) {
            for (PlantDetailsModel.Plant.Logs logs : logsList) {
                sortedList.add(logs);
            }
        }

        List<PlantDetailsModel.Plant.Issues> issuesList = plant.getIssues();
        if (issuesList != null && issuesList.size() > 0) {
            for (PlantDetailsModel.Plant.Issues issues : issuesList) {
                sortedList.add(issues);
            }
        }

        Collections.sort(sortedList, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {

                DateUtil dateUtil = new DateUtil();
                if (lhs instanceof PlantDetailsModel.Plant.Logs && rhs instanceof
                        PlantDetailsModel.Plant.Logs) {
                    long lhsCreatedTimeInMillis = dateUtil.getTimeInMillisFromTimeStamp(
                            ((PlantDetailsModel.Plant.Logs) lhs).getCreatedAt());

                    long rhsCreatedTimeInMillis = dateUtil.getTimeInMillisFromTimeStamp(
                            ((PlantDetailsModel.Plant.Logs) rhs).getCreatedAt());
                    return lhsCreatedTimeInMillis > rhsCreatedTimeInMillis ? -1 : 1;
                } else if (lhs instanceof PlantDetailsModel.Plant.Issues && rhs instanceof
                        PlantDetailsModel.Plant.Issues) {
                    long lhsCreatedTimeInMillis = dateUtil.getTimeInMillisFromTimeStamp(
                            ((PlantDetailsModel.Plant.Issues) lhs).getCreatedAt());

                    long rhsCreatedTimeInMillis = dateUtil.getTimeInMillisFromTimeStamp(
                            ((PlantDetailsModel.Plant.Issues) rhs).getCreatedAt());
                    return lhsCreatedTimeInMillis > rhsCreatedTimeInMillis ? -1 : 1;
                } else if (lhs instanceof PlantDetailsModel.Plant.Issues && rhs instanceof
                        PlantDetailsModel.Plant.Logs) {
                    long lhsCreatedTimeInMillis = dateUtil.getTimeInMillisFromTimeStamp(
                            ((PlantDetailsModel.Plant.Issues) lhs).getCreatedAt());

                    long rhsCreatedTimeInMillis = dateUtil.getTimeInMillisFromTimeStamp(
                            ((PlantDetailsModel.Plant.Logs) rhs).getCreatedAt());
                    return lhsCreatedTimeInMillis > rhsCreatedTimeInMillis ? -1 : 1;
                } else if (lhs instanceof PlantDetailsModel.Plant.Logs && rhs instanceof
                        PlantDetailsModel.Plant.Issues) {
                    long lhsCreatedTimeInMillis = dateUtil.getTimeInMillisFromTimeStamp(
                            ((PlantDetailsModel.Plant.Logs) lhs).getCreatedAt());

                    long rhsCreatedTimeInMillis = dateUtil.getTimeInMillisFromTimeStamp(
                            ((PlantDetailsModel.Plant.Issues) rhs).getCreatedAt());
                    return lhsCreatedTimeInMillis > rhsCreatedTimeInMillis ? -1 : 1;
                }
                return 0;
            }
        });
        return sortedList;
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


        plantName = plant.getName();
        description = plant.getDescription();
        plantImageUrl = Constants.BASE_URL + plant.getImages().get(0).getImage().getUrl();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                new DisplayUtil(PlantDetailsActivity.this).getImageHeight());
        ivPlantImage.setLayoutParams(layoutParams);
        ImageLoader.getInstance().displayImage(plantImageUrl,
                ivPlantImage, options, null);

        tvTitle.setText(plantName);

        tvDescription.setText(description);

        tvDate.setText(DateUtil.getFormattedDateFromTimeStamp(plant.getUpdatedAt(),
                DateUtil.DATE_FORMAT_DD_MMM_YYYY_HH_MM));
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

        List<Object> sortedList = sortLogsAndIssues();

        if (sortedList != null && sortedList.size() > 0) {
            llLogHolder.removeAllViews();
            TextView textView = new TextView(this);
            textView.setText(getString(R.string.logs));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextColor(getResources().getColor(R.color.text_color_green));
            textView.setTextAppearance(this, android.R.style.TextAppearance_Large);
            llLogHolder.addView(textView);
            View view = null;
            for (int i = 0; i < sortedList.size(); i++) {
                if (sortedList.get(i) instanceof PlantDetailsModel.Plant.Logs) {
                    PlantDetailsModel.Plant.Logs logs
                            = (PlantDetailsModel.Plant.Logs) sortedList.get(i);

                    view = LayoutInflater.from(PlantDetailsActivity.this)
                            .inflate(R.layout.log_list_item, null);

                    TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                    tvTitle.setText(logs.getContent());

                    TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
                    tvDate.setText(DateUtil.getFormattedDateFromTimeStamp(logs.getUpdatedAt(),
                            DateUtil.DATE_FORMAT_DD_MMM));

                    ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                    if (logs.getImages().size() > 0) {
                        ImageLoader.getInstance().displayImage(
                                Constants.BASE_URL + logs.getImages().get(0).getImage().getUrl(),
                                imageView, options, null);
                    }
                } else if (sortedList.get(i) instanceof PlantDetailsModel.Plant.Issues) {
                    PlantDetailsModel.Plant.Issues issues
                            = (PlantDetailsModel.Plant.Issues) sortedList.get(i);
                    view = LayoutInflater.from(PlantDetailsActivity.this)
                            .inflate(R.layout.image_text_list_item, null);

                    TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                    tvTitle.setText(issues.getTitle());
                    tvTitle.setTextColor(ColorUtil.getColorBasedOnStatus(this, issues.getStatus()));

                    TextView tvDescription = (TextView) view.findViewById(R.id.tvDescription);
                    tvDescription.setText(issues.getDescription());

                    TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
                    tvDate.setText(DateUtil.getFormattedDateFromTimeStamp(issues.getUpdatedAt(),
                            DateUtil.DATE_FORMAT_DD_MMM));

                    ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
                    if (issues.getImages().size() > 0) {
                        ImageLoader.getInstance().displayImage(
                                Constants.BASE_URL + issues.getImages().get(0).getImage().getUrl(),
                                imageView, options, null);
                    }
                }
                if (view != null) {
                    llLogHolder.addView(view);
                }
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
                Toast.makeText(PlantDetailsActivity.this, getString(R.string.error_msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private JSONObject getBody() {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(PlantDetailsActivity.this);
        }
        String phoneNumber = sharedPreferenceManager
                .getStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER,
                    phoneNumber);
            jsonObject.put(Constants.REQUEST_KEY_PLANT_ID, plantId);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return jsonObject;
    }

    private void deletePlant() {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.DELETE_PLANT_URL);
        webService.setBody(getBody());
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                finish();
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(PlantDetailsActivity.this, getString(R.string.error_msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void buildAlertDialogForDelete() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirmation_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        deletePlant();

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
