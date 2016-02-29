package com.webonise.gardenIt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.adapters.IssuesServicesRecyclerViewAdapter;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.models.IssuesListModel;
import com.webonise.gardenIt.models.ServiceListModel;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.SharedPreferenceManager;
import com.webonise.gardenIt.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class IssueServiceListActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private SharedPreferenceManager sharedPreferenceManager;
    private IssuesListModel issuesListModel;
    private ServiceListModel serviceListModel;
    private static int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setListView();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
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
        fetchUserIssues();
        AppController application = AppController.getInstance();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(type == Constants.CREATE_ISSUE ?
                Constants.ScreenName.ADVICE_LIST_SCREEN
                : Constants.ScreenName.REQUESTED_SERVICE_LIST_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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

    private void fetchUserIssues() {
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
                                if (issuesListModel.getIssues().size() > 0) {
                                    if (sharedPreferenceManager == null) {
                                        sharedPreferenceManager = new
                                                SharedPreferenceManager
                                                (IssueServiceListActivity.this);
                                    }
                                    sharedPreferenceManager.putObject(Constants
                                                    .KEY_PREF_USER_ISSUES,

                                            issuesListModel);
                                } else {
                                    showEmptyView();
                                    return;
                                }
                            }
                        } else {
                            serviceListModel = new Gson().fromJson(response,
                                    ServiceListModel.class);
                            if (serviceListModel.getStatus() == Constants.RESPONSE_CODE_200) {
                                if (serviceListModel.getRequests().size() > 0) {
                                    if (sharedPreferenceManager == null) {
                                        sharedPreferenceManager = new SharedPreferenceManager
                                                (IssueServiceListActivity.this);
                                    }
                                    sharedPreferenceManager.putObject(Constants
                                                    .KEY_PREF_USER_REQUEST,
                                            serviceListModel);
                                } else {
                                    showEmptyView();
                                    return;
                                }
                            }
                        }
                        setListView();
                        setDataInAdapter();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        error.printStackTrace();

                        Toast.makeText(IssueServiceListActivity.this, getString(R.string.error_msg),
                                Toast.LENGTH_LONG).show();
                    }
                }

        );
    }

    private JSONObject getBody() {
        if (sharedPreferenceManager == null) {
            sharedPreferenceManager = new SharedPreferenceManager(IssueServiceListActivity.this);
        }
        String phoneNumber = sharedPreferenceManager
                .getStringValue(Constants.KEY_PREF_USER_PHONE_NUMBER);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.REQUEST_KEY_PHONE_NUMBER,
                    phoneNumber);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * Method to show Empty view in case of no issues or service requests.
     */
    private void showEmptyView() {
        setContentView(R.layout.empty_issue_or_service_view);
        setToolbar();

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        TextView textView2 = (TextView) findViewById(R.id.textView2);

        if (type == Constants.CREATE_ISSUE) {
            imageView.setImageResource(R.drawable.empty_issue_icon);
            textView1.setText(R.string.no_issues);
            textView2.setText(R.string.no_issues_desc);

        } else {
            imageView.setImageResource(R.drawable.empty_service_request_icon);
            textView1.setText(R.string.no_service);
            textView2.setText(R.string.no_service_desc);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    /**
     * Method to initialize and set Data in adapter
     */
    private void setDataInAdapter() {
        IssuesServicesRecyclerViewAdapter
                issuesServicesRecyclerViewAdapter
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

    private void setListView(){
        setContentView(R.layout.activity_service_issues_list);
        setToolbar();
        ButterKnife.bind(this);
        fab.setOnClickListener(this);
    }
}
