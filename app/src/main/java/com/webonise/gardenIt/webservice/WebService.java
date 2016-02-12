package com.webonise.gardenIt.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.webonise.gardenIt.AppController;
import com.webonise.gardenIt.R;
import com.webonise.gardenIt.interfaces.ApiResponseInterface;
import com.webonise.gardenIt.utilities.Constants;
import com.webonise.gardenIt.utilities.LogUtils;
import com.webonise.gardenIt.utilities.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WebService {
    final static String TAG = "WebService";
    private Context mContext;
    private HashMap<String, String> header;
    private HashMap<String, String> params;
    private JSONObject body;
    private ProgressDialog progressDialog;
    private String url;
    private boolean showErrMsg;
    final int socketTimeout = 30000;
    public static final int JSON_OBJECT_REQUEST = 1;
    public static final int JSON_ARRAY_REQUEST = JSON_OBJECT_REQUEST + 1;


    public WebService(Context context) {
        this.mContext = context;
        header = null;
        params = null;
        body = null;
        progressDialog = null;
        url = null;
        showErrMsg = true;
        HttpsTrustManager.allowAllSSL();
    }

    public void setShowErrMsg(boolean showErrMsg) {
        this.showErrMsg = showErrMsg;
    }

    public void setHeader(HashMap<String, String> header) {
        this.header = header;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public void setBody(JSONObject body) {
        this.body = body;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setProgressDialog() {
        setProgressDialog(R.string.loading);
    }

    public void setProgressDialog(int message) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(message));
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void setProgressDialog(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public void POSTStringRequest(ApiResponseInterface responseInterface) {

        if (NetworkUtil.checkNetwork(mContext, true)) {
            if (progressDialog != null)
                progressDialog.show();
            makeRequest(responseInterface, Method.POST);
        }
    }


    public void PUTStringRequest(ApiResponseInterface responseInterface) {

        if (progressDialog != null)
            progressDialog.show();
        makeRequest(responseInterface, Method.PUT);
    }

    public void GETStringRequest(ApiResponseInterface responseInterface) {

        if (progressDialog != null)
            progressDialog.show();
        makeRequest(responseInterface, Method.GET);
    }

    public void JSONRequest(ApiResponseInterface responseInterface, JSONObject jsonRequestParam,
                            int type) {
        if (NetworkUtil.checkNetwork(mContext, true)) {
            if (progressDialog != null)
                progressDialog.show();
            if (type == JSON_ARRAY_REQUEST) {
                makeJsonArrayRequest(responseInterface, jsonRequestParam);
            } else {
                makeJsonRequest(responseInterface, jsonRequestParam);
            }
        }
    }

    private void makeRequest(final ApiResponseInterface responseInterface, int method) {
        StringRequest stringRequest = new StringRequest(method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtils.LOGD(TAG, "onResponse :: " + response);
                        if (progressDialog != null)
                            progressDialog.dismiss();
                        responseInterface.onResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.LOGD(TAG, "onErrorResponse :: " + error);
                if (progressDialog != null)
                    progressDialog.dismiss();
                responseInterface.onError(error);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (header != null)
                    return header;
                else
                    return getDefaultHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (params != null)
                    return params;
                else
                    return super.getParams();
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                if (body != null)
                    return body.toString().getBytes();
                else
                    return super.getBody();
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                if (volleyError.networkResponse != null && volleyError.networkResponse.data !=
                        null) {
                    return super.parseNetworkError(volleyError);
                }
                return super.parseNetworkError(volleyError);
            }
        };

        addToRequestQueue(stringRequest);
    }

    private void addToRequestQueue(Request request) {
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy
                .DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private void makeJsonRequest(final ApiResponseInterface responseInterface, JSONObject
            requestJson) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, requestJson, new
                Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                try {
                    LogUtils.LOGD(TAG, "onResponse :: " + response);
                    responseInterface.onResponse(response.toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.LOGD(TAG, "onErrorResponse :: " + error);

                Toast.makeText(mContext, mContext.getString(R.string.error_msg), Toast
                        .LENGTH_LONG).show();
                responseInterface.onError(error);
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
        addToRequestQueue(jsonObjectRequest);
    }

    private void makeJsonArrayRequest(final ApiResponseInterface responseInterface, JSONObject
            requestJson) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Method.POST, url, requestJson,
                new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                try {
                    LogUtils.LOGD(TAG, "onResponse :: " + response);
                    responseInterface.onResponse(response.toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.LOGD(TAG, "onErrorResponse :: " + error);

                Toast.makeText(mContext, mContext.getString(R.string.error_msg), Toast
                        .LENGTH_LONG).show();
                responseInterface.onError(error);
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
        addToRequestQueue(jsonArrayRequest);
    }

    private HashMap<String, String> getDefaultHeaders() {
        header = new HashMap<>();
        header.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        header.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);
        return header;
    }
}