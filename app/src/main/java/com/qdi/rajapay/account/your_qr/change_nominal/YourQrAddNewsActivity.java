package com.qdi.rajapay.account.your_qr.change_nominal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.account.your_qr.YourQrIndexActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 8.8 QR Anda
 * @screen 8.8.3
 */
public class YourQrAddNewsActivity extends BaseActivity {
    EditText news;
    Button submit;

    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_your_qr_add_news);

        init_toolbar(getResources().getString(R.string.activity_title_news));
        init();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(news.getText().toString().equals(""))
                        show_error_message(layout,getResources().getString(R.string.account_your_qr_enter_news_empty));
                    else if(news.getText().length() > 100)
                        show_error_message(layout,getResources().getString(R.string.account_your_qr_enter_news_too_long));
                    else {
                        data = new JSONObject(getIntent().getStringExtra("data"));
                        data.put("news", news.getText().toString());

                        submit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(){
        news = findViewById(R.id.news);
        submit = findViewById(R.id.submit);
    }

    private void submit() throws JSONException {
        JSONObject arr = new JSONObject();
        arr.put("amount",data.getDouble("data"));
        arr.put("desc",news.getText().toString());
        arr.put("idLogin",user_SP.getString("idLogin",""));
        arr.put("idUser",user_SP.getString("idUser",""));
        arr.put("token",user_SP.getString("token",""));

        url = BASE_URL+"/mobile/account/config-qr";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {

                        dismiss_wait_modal();
                        finish();
                        Intent intent = new Intent(YourQrAddNewsActivity.this, YourQrIndexActivity.class);
                        response_data.put("dMsg", getStr(R.string.s_nominalprocess_ok));
                        intent.putExtra("data", response_data.toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);

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