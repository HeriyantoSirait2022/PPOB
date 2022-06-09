package com.qdi.rajapay.account.manage_account.edit_password;

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
 * @module 8.4 Password Akin
 * @screen 8.4.2
 */
public class EditPasswordConfirmationActivity extends BaseActivity {
    EditText password;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit_password_confirmation);

        init_toolbar(getResources().getString(R.string.account_manage_account_change_password_confirm_toolbar));
        init();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(""))
                    show_error_message(layout,getResources().getString(R.string.confirm_password_empty_label));
                else if(!password.getText().toString().equals(
                        getIntent().getStringExtra("password")
                ))
                    show_error_message(layout,getResources().getString(R.string.password_not_same_confirm_label));
                else{
                    try {
                        submit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        init_show_password(password);
    }

    private void init(){
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
    }

    private void submit() throws JSONException {
        JSONObject arr = new JSONObject();
        arr.put("newPassword",password.getText().toString());
        arr.put("confirmNewPassword",password.getText().toString());
        arr.put("idLogin",user_SP.getString("idLogin",""));
        arr.put("idUser",user_SP.getString("idUser",""));
        arr.put("token",user_SP.getString("token",""));

        url = BASE_URL+"/mobile/account/change-password";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        finish();

                        Intent intent = new Intent(EditPasswordConfirmationActivity.this, ManageAccountIndexActivity.class);
                        response_data.put("dMsg", getStr(R.string.s_password_change));
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