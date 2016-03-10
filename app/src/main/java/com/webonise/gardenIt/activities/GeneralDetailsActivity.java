package com.webonise.gardenIt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
import com.webonise.gardenIt.models.GeneralDetailsModel;
import com.webonise.gardenIt.models.PlantDetailsModel;
import com.webonise.gardenIt.utilities.ColorUtil;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.DateUtil;
import com.webonise.gardenIt.utilities.ImageUtil;
import com.webonise.gardenIt.utilities.LogUtils;
import com.webonise.gardenIt.utilities.ShareUtil;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class GeneralDetailsActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    @Bind(R.id.ivPlantImage)
    ImageView ivPlantImage;
    @Bind(R.id.ivShare)
    ImageView ivShare;
    @Bind(R.id.tvDescription)
    TextView tvDescription;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvHeading)
    TextView tvHeading;
    @Bind(R.id.tvStatus)
    TextView tvStatus;
    @Bind(R.id.tvDate)
    TextView tvDate;
    @Bind(R.id.llStatus)
    LinearLayout llStatus;

    private int type;
    private int id;
    private String imageUrl, heading;
    private GeneralDetailsModel generalDetailsModel;
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_general_details);
        ButterKnife.bind(this);
        setToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBundleData();
        sendAnalyticsData();
        if (type == Constants.TYPE_LOGS) {
            setLogDetails();
        } else {
            getDetails();
        }
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

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt(Constants.BUNDLE_KEY_ID);
            type = bundle.getInt(Constants.BUNDLE_KEY_TYPE);
        }
    }

    private void sendAnalyticsData() {
        AppController application = AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getScreenName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private String getScreenName() {
        switch (type) {
            case Constants.TYPE_ADVICE:
                return Constants.ScreenName.ADVICE_DETAILS_SCREEN;
            case Constants.TYPE_SERVICE:
                return Constants.ScreenName.SERVICE_DETAILS_SCREEN;
            case Constants.TYPE_LOGS:
                return Constants.ScreenName.LOG_DETAILS_SCREEN;
            default:
                return Constants.ScreenName.ADVICE_DETAILS_SCREEN;
        }
    }

    private void setData() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.logo)
                .showImageOnFail(R.drawable.logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();

        AppController.getInstance().setupUniversalImageLoader(this);
        imageUrl = Constants.BASE_URL + generalDetailsModel.getImages().get(0).getImage().getUrl();
        ImageLoader.getInstance().displayImage(imageUrl, ivPlantImage, options, null);

        tvDescription.setText(generalDetailsModel.getDescription());
        heading = generalDetailsModel.getTitle();
        tvHeading.setText(heading);
        tvDate.setText(DateUtil.getFormattedDateFromTimeStamp(generalDetailsModel.getUpdatedAt(),
                DateUtil.DATE_FORMAT_DD_MMM_YYYY_HH_MM));
        String status = generalDetailsModel.getStatus();
        tvStatus.setText(" " + status.substring(0, 1).toUpperCase() + status.substring(1));
        tvStatus.setTextColor(ColorUtil.getColorBasedOnStatus(this, status));
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtil shareUtil = new ShareUtil(GeneralDetailsActivity.this);
                shareUtil.shareContent(shareUtil.getLocalBitmapUri(ivPlantImage));
            }
        });
        tvTitle.setText(type == Constants.TYPE_ADVICE ? getString(R.string.my_issues)
                : getString(R.string.service_requests));

        ivPlantImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GeneralDetailsActivity.this, ActivityImageView.class);
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
                        putExtra(Constants.BUNDLE_KEY_TITLE, heading).
                        putExtra(Constants.BUNDLE_KEY_IMAGE_URL, imageUrl);

                //Start details activity
                startActivity(intent);
            }
        });
    }

    private void getDetails() {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(type == Constants.TYPE_ADVICE ? Constants.GET_ADVICE_DETAILS_URL :
                Constants.GET_REQUEST_DETAILS_URL);
        webService.setBody(getBody());
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                generalDetailsModel = new Gson().fromJson(response,
                        GeneralDetailsModel.class);
                LogUtils.LOGD(TAG, response);
                setData();
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(GeneralDetailsActivity.this, getString(R.string.error_msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private JSONObject getBody() {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(GeneralDetailsActivity.this);
        }
        String phoneNumber = sharedPreferenceManager
                .getStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER,
                    phoneNumber);
            jsonObject.put(Constants.REQUEST_KEY_ID, id);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return jsonObject;
    }

    private void setLogDetails() {
        tvTitle.setText(getString(R.string.log));
        llStatus.setVisibility(View.GONE);
        tvDescription.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            tvHeading.setText(bundle.getString(Constants.BUNDLE_KEY_TITLE));
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.logo)
                    .showImageForEmptyUri(R.drawable.logo)
                    .showImageOnFail(R.drawable.logo)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new SimpleBitmapDisplayer())
                    .build();
            AppController.getInstance().setupUniversalImageLoader(this);
            ImageLoader.getInstance().displayImage(Constants.BASE_URL
                    + bundle.getString(Constants.BUNDLE_KEY_IMAGE_URL), ivPlantImage, options, null);
            tvDate.setText(DateUtil.getFormattedDateFromTimeStamp(bundle.getString(Constants
                    .BUNDLE_KEY_UPDATED_AT), DateUtil.DATE_FORMAT_DD_MMM_YYYY_HH_MM));
        }
    }
}
