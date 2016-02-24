package com.webonise.gardenIt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.utilities.ColorUtil;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.ShareUtil;

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

    private int type;

    private String plantPicUrl, description, heading, status;

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
        setData();
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
            type = bundle.getInt(Constants.BUNDLE_KEY_TYPE);
            plantPicUrl = bundle.getString(Constants.BUNDLE_KEY_IMAGE_URL);
            heading = bundle.getString(Constants.BUNDLE_KEY_TITLE);
            description = bundle.getString(Constants.BUNDLE_KEY_DESC);
            status = bundle.getString(Constants.BUNDLE_KEY_STATUS);
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
        ImageLoader.getInstance().displayImage(plantPicUrl, ivPlantImage, options, null);

        tvDescription.setText(description);
        tvHeading.setText(heading);
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
                        putExtra(Constants.BUNDLE_KEY_IMAGE_URL, plantPicUrl);

                //Start details activity
                startActivity(intent);
            }
        });
    }
}
