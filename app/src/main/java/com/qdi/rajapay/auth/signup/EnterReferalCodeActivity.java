package com.qdi.rajapay.auth.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.auth.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 2.0 Daftar
 * @screen 2.5
 */
public class EnterReferalCodeActivity extends BaseActivity {
    EditText refCode;
    Button next, skip;
    TextView signup;

    JSONObject data;
    boolean from_system = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_enter_referal_code);

        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        action();
    }

    private void init() throws JSONException {
        refCode = findViewById(R.id.referral_code);
        next = findViewById(R.id.next);
        skip = findViewById(R.id.skip);
        signup = findViewById(R.id.signup);
        layout = findViewById(R.id.layout);

        InputFilter[] editFilters = refCode.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        refCode.setFilters(newFilters);
        refCode.setInputType(refCode.getInputType()
                        | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                        | EditorInfo.TYPE_TEXT_VARIATION_FILTER
                        | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        refCode.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        refCode.requestFocus();

        data = new JSONObject(getIntent().getStringExtra("data"));
    }

    private void action(){
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(refCode.getText().toString().equals(""))
                    show_error_message(layout,getResources().getString(R.string.signup_enter_referal_empty));
                else if(refCode.getText().length() < 6)
                    show_error_message(layout,getResources().getString(R.string.signup_enter_referal_digit));
                else {
                    try {
                        submit("with_referral");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    submit("skip");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EnterReferalCodeActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
    }

    private void submit(String type) throws JSONException {
        String referal = refCode.getText().toString();

        JSONObject arr = new JSONObject();
        arr.put("noHp",data.getString("phone"));
        arr.put("shopNm",data.getString("name"));
        arr.put("password",data.getString("password"));
        if(!type.equals("skip"))
            arr.put("cdeReferral",referal);
        arr.put("idDevice",android_id);
        arr.put("ip",ip_address);

        url = BASE_URL+"/mobile/auth/register";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        user_edit_SP.putString("token",response_data.getString("token"));
                        user_edit_SP.putString("idLogin",response_data.getString("idLogin"));
                        user_edit_SP.putString("idUser",response_data.getString("idUser"));
                        if(response_data.has("verifyKey"))
                            user_edit_SP.putString("verifyKey",response_data.getString("verifyKey"));
                        user_edit_SP.commit();

                        get_data();
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

    private void get_data() {
        url = BASE_URL+"/mobile/account/user-info";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        user_edit_SP.putString("user",response_data.toString());
                        user_edit_SP.commit();

                        startActivity(new Intent(EnterReferalCodeActivity.this, SuccessActivity.class));
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
                    dismiss_wait_modal();
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