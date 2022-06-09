package com.qdi.rajapay.auth.login;

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

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.auth.signup.EnterPhoneNumberActivity;
import com.qdi.rajapay.home.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 3.0 Masuk
 * @screen 3.1
 */
public class LoginActivity extends BaseActivity {
    CoordinatorLayout layout;
    EditText phone_no, password;
    TextView signup, forgot_password;
    ImageView phone_no_icon_left,phone_no_icon_right;
    LinearLayout phone_no_layout;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!user_SP.getString("token","").equals(""))
            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

        init();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    login();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, EnterPhoneNumberActivity.class)
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

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    if(phone_no.getText().toString().equals(""))
//                        show_error_message(layout,getResources().getString(R.string.phone_empty_label));
//                    else {
//                        JSONObject data = new JSONObject();
//                        data.put("phone", phone_no.getText().toString());

//                        startActivity(new Intent(LoginActivity.this, OtpActivity.class)
//                                .putExtra("data", data.toString()));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                ForgetPasswordModal modal = new ForgetPasswordModal();
                modal.show(getSupportFragmentManager(),"modal");
            }
        });

        init_show_password(password);
    }

    private void init() {
        phone_no = findViewById(R.id.phone_no);
        password = findViewById(R.id.password);
        next = findViewById(R.id.next);
        signup = findViewById(R.id.signup);
        layout = findViewById(R.id.layout);
        phone_no_icon_left = findViewById(R.id.phone_no_icon_left);
        phone_no_icon_right = findViewById(R.id.phone_no_icon_right);
        phone_no_layout = findViewById(R.id.phone_no_layout);
        forgot_password = findViewById(R.id.forgot_password);
    }

    private void login() throws JSONException {
        if(phone_no.getText().toString().equals(""))
            show_error_message(phone_no,getResources().getString(R.string.phone_empty_label));
        else if(password.getText().toString().equals(""))
            show_error_message(password,getResources().getString(R.string.password_empty_label));
        else if(phone_no.getText().length() > 25)
            show_error_message(phone_no,getResources().getString(R.string.signup_enter_phone_maximum_length_exceed));
        else {
            JSONObject arr = new JSONObject();
            arr.put("noHp",phone_no.getText().toString());
            arr.put("password",password.getText().toString());
            arr.put("idDevice",android_id);
            arr.put("ip",ip_address);
            if(!user_SP.getString("verifyKey","").equals(""))
                arr.put("verifyKey",user_SP.getString("verifyKey",""));

            url = BASE_URL+"/mobile/auth/login";
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

                            JSONObject data = new JSONObject();
                            data.put("phone",phone_no.getText().toString());

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

                        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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