package com.qdi.rajapay;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.auth.login.LoginActivity;
import com.qdi.rajapay.home.MainActivity;
import com.qdi.rajapay.onboarding.OnBoardOne;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        layout = findViewById(R.id.layout);
        try {
            get_data();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void redirect(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user_SP.getBoolean("is_first_time",true))
                    startActivity(new Intent(SplashActivity.this, OnBoardOne.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                else if(user_SP.getString("token","").equals(""))
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                else if(!user_SP.getString("idUser", "").isEmpty() && !user_SP.getString("token", "").isEmpty() && !user_SP.getString("idLogin", "").isEmpty()) {
                    Intent intent = getIntent();

                    if(intent.hasExtra("notificationCaller") && intent.getBooleanExtra("notificationCaller", false)){
                        startActivity(intent);
                    }else{
                        startActivity(new Intent(SplashActivity.this, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                }
                else
                    displaySnackBar(getStr(R.string.f_login_problem));
            }
        },delay_time_for_alert);
    }

    public void updateApp(){
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            finish();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void get_data() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

        url = BASE_URL+"/mobile/auth/version-check?idVersion="+pInfo.versionName;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        redirect();
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if(error.networkResponse != null){
                        String response_data = new String(error.networkResponse.data, "UTF-8");
                        JSONObject data = new JSONObject(response_data);

                        if (data.has("response")) {
                            JSONObject data_response = data.getJSONObject("response");

                            if(data_response.has("code")){
                                if(data_response.getInt("code") == 400){
                                    String message = error_handling(error, layout);

                                    Snackbar snackbar = Snackbar
                                            .make(layout, message, Snackbar.LENGTH_LONG)
                                            .setAction("UPDATE APP", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    updateApp();
                                                }
                                            });

                                    snackbar.addCallback(new Snackbar.Callback(){
                                                             @Override
                                                             public void onDismissed(Snackbar transientBottomBar, int event) {
                                                                 super.onDismissed(transientBottomBar, event);

                                                                 updateApp();
                                                             }
                                                         }
                                    );

                                    snackbar.show();

                                    return;
                                }
                            }
                        }
                    }

                    error_handling(error, layout);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        consume_api(jsonObjectRequest);

    }
}
