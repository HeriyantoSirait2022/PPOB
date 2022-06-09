package com.qdi.rajapay.auth.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * @screen 2.1
 */
public class EnterPhoneNumberActivity extends BaseActivity {
    EditText phone_no;
    Button next;
    TextView signup;
    ImageView phone_no_icon_left,phone_no_icon_right;
    LinearLayout phone_no_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_enter_phone_number);

        init();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                startActivity(new Intent(EnterPhoneNumberActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        phone_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    phone_no.setTextColor(getResources().getColor(R.color.blue_completed));
                    phone_no_icon_left.setImageDrawable(getDrawable(R.drawable.ic_phone_android_blue_completed_24));
                    phone_no_icon_right.setImageDrawable(getDrawable(R.drawable.ic_check_circle_outline_blue_completed_24));
                    phone_no_layout.setBackground(getDrawable(R.drawable.rounded_blue));
                }
                else{
                    phone_no.setTextColor(getResources().getColor(R.color.black));
                    phone_no_icon_left.setImageDrawable(getDrawable(R.drawable.ic_phone_android_black_24));
                    phone_no_icon_right.setImageDrawable(getDrawable(R.drawable.ic_check_circle_outline_black_24));
                    phone_no_layout.setBackground(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init(){
        phone_no = findViewById(R.id.phone_no);
        next = findViewById(R.id.next);
        signup = findViewById(R.id.signup);
        layout = findViewById(R.id.layout);
        phone_no_icon_left = findViewById(R.id.phone_no_icon_left);
        phone_no_icon_right = findViewById(R.id.phone_no_icon_right);
        phone_no_layout = findViewById(R.id.phone_no_layout);
    }

    private void submit() throws JSONException {
        if(phone_no.getText().toString().equals(""))
            show_error_message(layout,getResources().getString(R.string.phone_empty_label));
        else if(phone_no.getText().length() > 24)
            show_error_message(layout,getResources().getString(R.string.signup_enter_phone_maximum_length_exceed));
        else {
            url = BASE_URL+"/mobile/auth/register-check?noHp="+phone_no.getText().toString();
            show_wait_modal();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        dismiss_wait_modal();
                        JSONObject response_data = response.getJSONObject("response");
                        if (!response_data.getString("type").equals("Failed")) {
                            JSONObject data = new JSONObject();
                            data.put("phone",phone_no.getText().toString());
                            startActivity(new Intent(EnterPhoneNumberActivity.this, SignupOtpActivity.class)
                                    .putExtra("data", data.toString()));
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
}