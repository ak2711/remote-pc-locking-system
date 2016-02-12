package com.webonise.gardenIt.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.adapters.DashboardRecyclerViewAdapter;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.RecyclerViewItemDecorator;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = this.getClass().getName();
    private final int SPAN_COUNT = 2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.rlAddNewPlant)
    RelativeLayout rlAddNewPlant;
    @Bind(R.id.btnCreateIssue)
    Button btnRequestService;
    @Bind(R.id.btnRequestService)
    Button btnCreateIssue;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    TextView tvUserName, tvMobileNumber;

    private SharedPreferenceManager sharedPreferenceManager;
    private UserDashboardModel userDashboardModel;
    private String shopNowLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);
        ButterKnife.bind(this);
        rlAddNewPlant.setOnClickListener(this);
        btnRequestService.setOnClickListener(this);
        btnCreateIssue.setOnClickListener(this);
        setupNavigationDrawer();
        setToolbar();
        setUpRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Obtain the shared Tracker instance.
        AppController application = AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.ScreenName.DASHBOARD_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        fetchUserDashboardData();
    }

    private void setUpRecyclerView() {
        int margin = getResources().getDimensionPixelSize(R.dimen.margin_10);
        recyclerView.addItemDecoration(new RecyclerViewItemDecorator(SPAN_COUNT,
                margin, false));
        recyclerView.setHasFixedSize(true);
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(R.drawable.icon_hamburger);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            });
        }
    }

    private void setupNavigationDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.getHeaderView(0);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvMobileNumber = (TextView) view.findViewById(R.id.tvMobileNumber);
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
            case R.id.rlAddNewPlant:
                goToAddNewPlantActivity();
                break;
        }
    }

    private void fetchUserDashboardData() {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.SIGN_IN_URL);
        webService.setBody(getBody());
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                userDashboardModel = new Gson().fromJson(response,
                        UserDashboardModel.class);
                if (userDashboardModel.getStatus() == Constants.RESPONSE_CODE_200) {
                    if (sharedPreferenceManager == null) {
                        sharedPreferenceManager = new SharedPreferenceManager
                                (DashboardActivity.this);
                    }
                    sharedPreferenceManager.putObject(Constants.KEY_PREF_USER_GARDEN_PLANTS,
                            userDashboardModel);
                    sharedPreferenceManager.setStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER,
                            userDashboardModel.getUser().getPhoneNumber());
                    setTitle();
                    tvUserName.setText(userDashboardModel.getUser().getName());
                    tvMobileNumber.setText(userDashboardModel.getUser().getPhoneNumber());
                    try {
                        sharedPreferenceManager.setIntValue(Constants.KEY_PREF_GARDEN_ID,
                                userDashboardModel.getUser().getGardens().get(0).getId());
                        shopNowLink = userDashboardModel.getUser().getLinks().getStoreLink();
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    } catch (ArrayIndexOutOfBoundsException indexOutOfBoundException){
                        indexOutOfBoundException.printStackTrace();
                    }
                    DashboardRecyclerViewAdapter dashboardRecyclerViewAdapter
                            = new DashboardRecyclerViewAdapter(DashboardActivity.this);

                    //Set Span count to 2 as 2 items to show in a row.
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(
                            DashboardActivity.this, SPAN_COUNT);

                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(dashboardRecyclerViewAdapter);

                } else {
                    Toast.makeText(DashboardActivity.this, userDashboardModel.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();

                Toast.makeText(DashboardActivity.this, getString(R.string.error_msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private JSONObject getBody() {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(DashboardActivity.this);
        }
        String phoneNumber = sharedPreferenceManager
                .getStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER);
        String password = sharedPreferenceManager
                .getStringValue(Constants.KEY_PREF_USER_PASSWORD);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER,
                    phoneNumber);
            jsonObject.put(Constants.REQUEST_KEY_PASSWORD, password);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return jsonObject;
    }

    private void goToAddNewPlantActivity() {
        Intent intent = new Intent();
        intent.setClass(DashboardActivity.this, AddPlantActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY_SHOW_BACK_ICON, true);
        startActivity(intent);
    }

    private void goToCreateIssueActivity() {
        Intent intent = new Intent();
        intent.setClass(DashboardActivity.this, CreateIssueActivity.class);
        startActivity(intent);
    }

    private void goToServiceRequestActivity() {
        Intent intent = new Intent();
        intent.setClass(DashboardActivity.this, RequestServiceActivity.class);

        UserDashboardModel.User.Gardens lastGarden = getLastGardenDetails();
        if (lastGarden != null) {
            intent.putExtra(Constants.BUNDLE_KEY_GARDEN_ID, lastGarden.getId());
        }

        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            /*case R.id.editGarden:
                break;*/
            case R.id.dashboard:
                break;
            case R.id.myIssues:
                Intent myIssuesIntent = new Intent(DashboardActivity.this,
                        IssueServiceListActivity.class);
                myIssuesIntent.putExtra(Constants.BUNDLE_KEY_TYPE, Constants.CREATE_ISSUE);
                startActivity(myIssuesIntent);
                break;
            case R.id.serviceRequests:
                Intent serviceRequestIntent = new Intent(DashboardActivity.this,
                        IssueServiceListActivity.class);
                serviceRequestIntent.putExtra(Constants.BUNDLE_KEY_TYPE, Constants.REQUEST_SERVICE);
                startActivity(serviceRequestIntent);
                break;
            case R.id.shopNow:
                if (!TextUtils.isEmpty(shopNowLink)) {
                    Intent shopNowIntent = new Intent(Intent.ACTION_VIEW);
                    shopNowIntent.setData(Uri.parse(shopNowLink));
                    startActivity(shopNowIntent);
                } else {
                    Toast.makeText(this, getString(R.string.yet_to_be_implemented),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void setTitle() {
        UserDashboardModel.User.Gardens lastGarden = getLastGardenDetails();
        if (lastGarden != null) {
            tvTitle.setText(lastGarden.getName());
        } else {
            tvTitle.setText(getString(R.string.dashboard));
        }
    }

    private UserDashboardModel.User.Gardens getLastGardenDetails() {
        if (userDashboardModel != null) {
            try{
                List<UserDashboardModel.User.Gardens> gardensList
                        = userDashboardModel.getUser().getGardens();
                return gardensList.get(gardensList.size() - 1);
            } catch (Exception e){
               e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}