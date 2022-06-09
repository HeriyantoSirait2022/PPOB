package com.qdi.rajapay.account.manage_account.edit_email;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.concurrent.TimeUnit;

/**
 * @module 8.3 Email Akun
 * @screen 8.3.2
 */
public class EditEmailEnterOtpActivity extends BaseActivity {
    EditText otp;
    Button submit, resend;
    TextView send_to;

    private static final String FORMAT = "%02d:%02d";
    boolean allow_resend = true;
    JSONObject profile_data;

    CountDownTimer countDownTimer = new CountDownTimer(120000, 1000) {

        public void onTick(long millisUntilFinished) {
            // resend.setText(getResources().getString(R.string.otp_resend) + "(" + millisUntilFinished / 1000 + ")");
            resend.setText(getResources().getString(R.string.otp_resend) + " (" + String.format(FORMAT,
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + ")");
            allow_resend = false;
        }

        public void onFinish() {
            resend.setText(getResources().getString(R.string.otp_resend));
            allow_resend = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit_email_enter_otp);

        init_toolbar(getResources().getString(R.string.activity_title_otp_change_email));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        action();
    }

    private void init() throws JSONException {
        otp = findViewById(R.id.pin);
        submit = findViewById(R.id.submit);
        resend = findViewById(R.id.resend);
        send_to = findViewById(R.id.send_to);

        otp.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        profile_data = new JSONObject("{\"phone\":\"0810801801\"}");

        countDownTimer.start();
        send_to.setText(getResources().getString(R.string.account_manage_account_change_email_otp_send_to)+" "+getIntent().getStringExtra("email"));

        resend_otp();
    }

    private void action(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otp.getText().toString().equals(""))
                    show_error_message(layout,getResources().getString(R.string.otp_empty_label));
                else if(otp.getText().length() < 6)
                    show_error_message(layout,getResources().getString(R.string.otp_digit_label));
                else {
                    try {
                        submit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(allow_resend)
                        resend_otp();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void resend_otp() throws JSONException {
        JSONObject arr = new JSONObject();
        arr.put("newEmail",getIntent().getStringExtra("email"));
        arr.put("idLogin",user_SP.getString("idLogin",""));
        arr.put("idUser",user_SP.getString("idUser",""));
        arr.put("token",user_SP.getString("token",""));

        url = BASE_URL+"/mobile/account/change-emailotp";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        show_error_message(layout,getResources().getString(R.string.otp_notif_resend));
                        countDownTimer.start();
                    } else {
                        show_error_message(layout,response_data.getJSONObject("message").getJSONArray("errorInfo").getString(2));
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
                headers.put("Authorization", user_SP.getString("access_token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        consume_api(jsonObjectRequest);
    }

    private void submit() throws JSONException {
        JSONObject arr = new JSONObject();
        arr.put("newEmail",getIntent().getStringExtra("email"));
        arr.put("otp",otp.getText().toString());
        arr.put("idLogin",user_SP.getString("idLogin",""));
        arr.put("idUser",user_SP.getString("idUser",""));
        arr.put("token",user_SP.getString("token",""));

        url = BASE_URL+"/mobile/account/change-email";
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        Intent intent = new Intent(EditEmailEnterOtpActivity.this, ManageAccountIndexActivity.class);
                        response_data.put("dMsg", getStr(R.string.s_email_change));
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
                    error_handling(error, layout, "Kode OTP");
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