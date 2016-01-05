package com.webonise.gardenIt.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.adapters.DashboardRecyclerViewAdapter;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.CreateGardenModel;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = this.getClass().getName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.btnAddNewPlant)
    Button btnAddNewPlant;
    @Bind(R.id.btnRequestService)
    Button btnRequestService;
    @Bind(R.id.btnCreateIssue)
    Button btnCreateIssue;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        btnAddNewPlant.setOnClickListener(this);
        btnRequestService.setOnClickListener(this);
        btnCreateIssue.setOnClickListener(this);
        setToolbar();
        fetchUserDashboardData();
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
                break;
            case R.id.btnRequestService:
                break;
            case R.id.btnAddNewPlant:
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
                UserDashboardModel userDashboardModel = new Gson().fromJson(response,
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
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(DashboardActivity
                            .this, 2);

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
}
