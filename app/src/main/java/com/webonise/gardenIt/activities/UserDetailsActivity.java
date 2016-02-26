package com.webonise.gardenIt.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class UserDetailsActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.ivProfilePic)
    ImageView ivProfilePic;
    @Bind(R.id.tvFullName)
    TextView tvFullName;
    @Bind(R.id.tvPhoneNumber)
    TextView tvPhoneNumber;
    @Bind(R.id.tvEmail)
    TextView tvEmail;
    @Bind(R.id.tvReferredBy)
    TextView tvReferredBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_user_details);
        ButterKnife.bind(this);

        setToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Obtain the shared Tracker instance.
        AppController application = AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.ScreenName.USER_DETAILS_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        setUserDetails();
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tvTitle.setText(R.string.my_profile);
            toolbar.setNavigationIcon(R.drawable.ic_action_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void setUserDetails() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        UserDashboardModel userDashboardModel = sharedPreferenceManager.getObject(Constants
                .KEY_PREF_USER_GARDEN_PLANTS, UserDashboardModel.class);
        UserDashboardModel.User user = userDashboardModel.getUser();
        if (user != null) {
            tvFullName.setText(user.getName());
            tvPhoneNumber.setText(user.getPhoneNumber());
            tvEmail.setText(TextUtils.isEmpty(user.getEmail()) ? getString(R.string.na)
                    : user.getEmail());
            tvReferredBy.setText(user.getReferredBy());

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.icon_profile_pic)
                    .showImageForEmptyUri(R.drawable.icon_profile_pic)
                    .showImageOnFail(R.drawable.icon_profile_pic)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(500))
                    .build();

            AppController.getInstance().setupUniversalImageLoader(this);
            ImageLoader.getInstance().displayImage(
                    Constants.BASE_URL + user.getProfileImage(),
                    ivProfilePic, options, null);
        }
    }
}
