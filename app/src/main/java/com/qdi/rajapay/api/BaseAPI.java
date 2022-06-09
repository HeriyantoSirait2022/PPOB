package com.qdi.rajapay.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.qdi.rajapay.BuildConfig;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class BaseAPI {

    public static String BASE_URL = BuildConfig.SERVER_URL;

    protected RequestQueue queue;

    protected String idLogin;
    protected String idUser;
    protected String token;

    private BaseAPI() {
        throw new AssertionError();
    }

    public BaseAPI(Context context) {
        idLogin = "";
        idUser = "";
        token = "";
        queue = Volley.newRequestQueue(context);
    }

    public BaseAPI(Context context, SharedPreferences user_SP) {
        idLogin = user_SP.getString("idLogin","");
        idUser= user_SP.getString("idUser","");
        token= user_SP.getString("token","");
        queue = Volley.newRequestQueue(context);
    }

    protected Map<String, String> getCredentialParam() {
        Map<String, String> map = new HashMap<>();
        map.put("idLogin", idLogin);
        map.put("idUser", idUser);
        map.put("token", token);
        return map;
    }

    protected Response.ErrorListener onAPICallError(final APICallback.BaseAPICallback callback) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onAPIResponseFailure(error);
            }
        };
    }
}
