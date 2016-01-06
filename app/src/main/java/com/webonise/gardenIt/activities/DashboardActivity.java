package com.webonise.gardenIt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.adapters.DashboardRecyclerViewAdapter;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
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

    private SharedPreferenceManager sharedPreferenceManager;
    private UserDashboardModel userDashboardModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_new);
        ButterKnife.bind(this);
        rlAddNewPlant.setOnClickListener(this);
        btnRequestService.setOnClickListener(this);
        btnCreateIssue.setOnClickListener(this);
        setToolbar();
        fetchUserDashboardData();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            tvTitle.setText(R.string.my_garden);
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

                    DashboardRecyclerViewAdapter dashboardRecyclerViewAdapter
                            = new DashboardRecyclerViewAdapter(DashboardActivity.this);

                    //Set Span count to 2 as Add Member has 2 items to show.
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(
                            DashboardActivity.this, 2);

                    recyclerView.setHasFixedSize(true);
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
            }
        });
    }

    private JSONObject getBody() {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(DashboardActivity.this);
        }
        UserModel userModel = sharedPreferenceManager.getObject(
                Constants.KEY_PREF_USER, UserModel.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER,
                    userModel.getUser().getPhone_number());
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

        if (userDashboardModel != null) {
            List<UserDashboardModel.User.Gardens> gardensList
                    = userDashboardModel.getUser().getGardens();
            UserDashboardModel.User.Gardens lastGarden = gardensList.get(gardensList.size() - 1);
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

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
