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

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.adapters.IssuesServicesRecyclerViewAdapter;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.IssuesListModel;
import com.webonise.gardenIt.models.ServiceListModel;
import com.webonise.gardenIt.models.UserModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IssueServiceListActivity extends AppCompatActivity implements View.OnClickListener {

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
    private ServiceListModel serviceListModel;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_issues_list);
        ButterKnife.bind(this);
    }

    private void setToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            tvTitle.setText(type == Constants.CREATE_ISSUE ? R.string.my_issues
                    : R.string.service_requests);
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
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getInt(Constants.BUNDLE_KEY_TYPE);
        }
        setToolbar();
        fab.setOnClickListener(this);
        fetchUseIssues();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab:
                Intent intent = new Intent(IssueServiceListActivity.this,
                        type == Constants.CREATE_ISSUE
                                ? CreateIssueActivity.class : RequestServiceActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void fetchUseIssues() {
        WebService webService = new WebService(this);
        webService.setProgressDialog();
        webService.setUrl(type == Constants.CREATE_ISSUE
                ? Constants.ISSUES_LIST_URL : Constants.REQUEST_LIST_URL);
        webService.setBody(getBody());
        webService.POSTStringRequest(
                new ApiResponseInterface() {
                    @Override
                    public void onResponse(String response) {

                        if (type == Constants.CREATE_ISSUE) {
                            issuesListModel = new Gson().fromJson(response,
                                    IssuesListModel.class);
                            if (issuesListModel.getStatus() == Constants
                                    .RESPONSE_CODE_200) {
                                if (sharedPreferenceManager == null) {
                                    sharedPreferenceManager = new
                                            SharedPreferenceManager
                                            (IssueServiceListActivity.this);
                                }
                                sharedPreferenceManager.putObject(Constants
                                                .KEY_PREF_USER_ISSUES,
                                        issuesListModel);
                            }
                        } else {
                            serviceListModel = new Gson().fromJson(response,
                                    ServiceListModel.class);
                            if (serviceListModel.getStatus() == Constants
                                    .RESPONSE_CODE_200) {
                                if (sharedPreferenceManager == null) {
                                    sharedPreferenceManager = new
                                            SharedPreferenceManager
                                            (IssueServiceListActivity.this);
                                }
                                sharedPreferenceManager.putObject(Constants
                                                .KEY_PREF_USER_REQUEST,
                                        serviceListModel);
                            }
                        }
                        IssuesServicesRecyclerViewAdapter issuesServicesRecyclerViewAdapter
                                = new IssuesServicesRecyclerViewAdapter
                                (IssueServiceListActivity.this, type);

                        LinearLayoutManager linearLayoutManager = new
                                LinearLayoutManager(
                                IssueServiceListActivity.this,
                                LinearLayoutManager.VERTICAL, false);

                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(issuesServicesRecyclerViewAdapter);

                    }

                    @Override
                    public void onError(VolleyError error) {
                        error.printStackTrace();
                    }
                }

        );
    }

    private JSONObject getBody() {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(IssueServiceListActivity.this);
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
