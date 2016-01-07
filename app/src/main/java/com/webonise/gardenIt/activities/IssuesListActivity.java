package com.webonise.gardenIt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.adapters.DashboardRecyclerViewAdapter;
import com.webonise.gardenIt.adapters.IssuesRecyclerViewAdapter;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.IssuesListModel;
import com.webonise.gardenIt.models.UserDashboardModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IssuesListActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private SharedPreferenceManager sharedPreferenceManager;
    private IssuesListModel issuesListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues_list);
        ButterKnife.bind(this);
        setToolbar();
        fab.setOnClickListener(this);
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            tvTitle.setText(R.string.my_issues);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUseIssues();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab:
                Intent intent = new Intent(IssuesListActivity.this, CreateIssueActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void fetchUseIssues() {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(Constants.ISSUES_LIST_URL);
        webService.setBody(getBody());
        webService.POSTStringRequest(new ApiResponseInterface() {
            @Override
            public void onResponse(String response) {
                issuesListModel = new Gson().fromJson(response,
                        IssuesListModel.class);
                if (issuesListModel.getStatus() == Constants.RESPONSE_CODE_200) {
                    if (sharedPreferenceManager == null) {
                        sharedPreferenceManager = new SharedPreferenceManager
                                (IssuesListActivity.this);
                    }
                    sharedPreferenceManager.putObject(Constants.KEY_PREF_USER_ISSUES,
                            issuesListModel);
                    IssuesRecyclerViewAdapter issuesRecyclerViewAdapter
                            = new IssuesRecyclerViewAdapter(IssuesListActivity.this);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                            IssuesListActivity.this, LinearLayoutManager.VERTICAL, false);

                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(issuesRecyclerViewAdapter);

                } else {
                    Toast.makeText(IssuesListActivity.this, getString(R.string.please_try_again),
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
            sharedPreferenceManager = new SharedPreferenceManager(IssuesListActivity.this);
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
