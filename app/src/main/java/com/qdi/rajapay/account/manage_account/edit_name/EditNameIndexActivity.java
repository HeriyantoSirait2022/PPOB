package com.qdi.rajapay.account.manage_account.edit_name;

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
import com.qdi.rajapay.account.manage_account.ManageAccountIndexActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 8.2 Nama Toko
 * @screen 8.2.2
 */

public class EditNameIndexActivity extends BaseActivity {
    EditText name;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit_name);

        init_toolbar(getResources().getString(R.string.account_manage_account_change_name_subtitle));
        init();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals(""))
                    show_error_message(layout,getResources().getString(R.string.account_manage_account_change_name_empty_name));
                else if(name.getText().length() > 30)
                    show_error_message(layout,getResources().getString(R.string.account_manage_account_change_name_too_long));
                else{
                    try {
                        submit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void init(){
        name = findViewById(R.id.name);
        submit = findViewById(R.id.submit);
    }

    private void submit() throws JSONException {
        JSONObject arr = new JSONObject();
        arr.put("newShopNm",name.getText().toString());
        arr.put("idLogin",user_SP.getString("idLogin",""));
        arr.put("idUser",user_SP.getString("idUser",""));
        arr.put("token",user_SP.getString("token",""));

        url = BASE_URL+"/mobile/account/change-shopnm";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        finish();
                        Intent intent = new Intent(EditNameIndexActivity.this, ManageAccountIndexActivity.class);
                        response_data.put("dMsg", getStr(R.string.s_shopname_change));
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