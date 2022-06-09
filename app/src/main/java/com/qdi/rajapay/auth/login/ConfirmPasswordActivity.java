package com.qdi.rajapay.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.auth.ConditionPasswordAdapter;
import com.qdi.rajapay.auth.signup.EnterPhoneNumberActivity;
import com.qdi.rajapay.home.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 3.0 Masuk
 * @screen 3.4
 */
public class ConfirmPasswordActivity extends BaseActivity {
    RecyclerView list;
    EditText password;
    Button next;
    TextView signup;

    ConditionPasswordAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);

        try {
            init_toolbar("");
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    check_condition(array,charSequence);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmPasswordActivity.this, EnterPhoneNumberActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        init_show_password(password);
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        password = findViewById(R.id.password);
        next = findViewById(R.id.next);
        signup = findViewById(R.id.signup);

        array = add_condition_password();
        adapter = new ConditionPasswordAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        data = new JSONObject(getIntent().getStringExtra("data"));
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

                        Intent intent = new Intent(ConfirmPasswordActivity.this, MainActivity.class);
                        response_data.put("dMsg", getStr(R.string.s_password_change));
                        intent.putExtra("data", response_data.toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

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
        if(password.getText().toString().equals(""))
            show_error_message(layout,getResources().getString(R.string.confirm_password_empty_label));
        else if(!password.getText().toString().equals(getIntent().getStringExtra("password")))
            show_error_message(layout,getResources().getString(R.string.password_not_same_confirm_label));
        else{
            JSONObject arr = new JSONObject();
            arr.put("noHp",data.getString("phone"));
            arr.put("newPassword",password.getText().toString());
            arr.put("confirmNewPassword",password.getText().toString());
            arr.put("idDevice",android_id);
            /**
             * @author Cherry
             * @patch P2301
             * @notes add otp str to support forgot-password patch API
             */
            // <code>
            arr.put("otp", getIntent().getStringExtra("otp"));
            // </code>
            arr.put("ip",ip_address);

            url = BASE_URL+"/mobile/auth/forgot-password";
            show_wait_modal();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url, arr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject response_data = response.getJSONObject("response");
                        if (!response_data.getString("type").equals("Failed")) {
                            user_edit_SP.putString("token",response_data.getString("token"));
                            user_edit_SP.putString("idLogin",response_data.getString("idLogin"));
                            user_edit_SP.putString("idUser",response_data.getString("idUser"));
                            user_edit_SP.commit();

                            dismiss_wait_modal();

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
}