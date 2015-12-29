package com.webonise.urbanfarmers.interfaces;

import com.android.volley.VolleyError;

public interface ApiResponseInterface {

    void onResponse(String response);

    void onError(VolleyError error);
}
