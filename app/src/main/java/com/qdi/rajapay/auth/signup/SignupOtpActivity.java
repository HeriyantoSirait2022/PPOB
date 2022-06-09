package com.qdi.rajapay.auth.signup;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @module 2.0 Daftar
 * @screen 2.8++
 */
public class SignupOtpActivity extends BaseActivity {
    EditText otp;
    Button next;
    TextView signup, resend, info_send;

    JSONObject data;
    private static final String FORMAT = "%02d:%02d";
    boolean allow_resend = true;
    boolean from_system = false;

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
        setContentView(R.layout.activity_signup_otp);

        init_toolbar("");
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        action();
    }

    private void init() throws JSONException {
        otp = findViewById(R.id.otp);
        next = findViewById(R.id.next);
        signup = findViewById(R.id.signup);
        resend = findViewById(R.id.resend);
        info_send = findViewById(R.id.info_send);

        otp.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        otp.requestFocus();

        data = new JSONObject(getIntent().getStringExtra("data"));
        resend.setText(getResources().getString(R.string.otp_resend));
        info_send.setText(getResources().getString(R.string.otp_subtitle_1)+" "+data.getString("phone"));

        countDownTimer.start();
    }

    private void action(){
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    submit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupOtpActivity.this, EnterPhoneNumberActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
        url = BASE_URL+"/mobile/auth/register-check?noHp="+data.getString("phone");
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        show_error_message(layout,getResources().getString(R.string.otp_notif_resend));
                        countDownTimer.start();
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

    private void submit() throws JSONException {
        if(otp.getText().toString().equals("") )
            show_error_message(layout,getResources().getString(R.string.otp_empty_label));
        else if(otp.getText().length() < 6)
            show_error_message(layout,getResources().getString(R.string.otp_digit_label));
        else {
            String strOtp = otp.getText().toString();
            JSONObject arr = new JSONObject();
            arr.put("noHp",data.getString("phone"));
            arr.put("otp",strOtp);

            url = BASE_URL+"/mobile/auth/register-verifyotp";
            show_wait_modal();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        dismiss_wait_modal();
                        JSONObject response_data = response.getJSONObject("response");
                        if (!response_data.getString("type").equals("Failed")) {
                            finish();
                            startActivity(new Intent(SignupOtpActivity.this,  EnterNameActivity.class)
                                    .putExtra("data",data.toString()));
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
    }
}