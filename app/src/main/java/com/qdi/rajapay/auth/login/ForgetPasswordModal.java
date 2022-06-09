package com.qdi.rajapay.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ForgetPasswordModal extends DialogFragment {
    Button submit;
    EditText phone_no;

    LoginActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forget_password_input_no_modal,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(MATCH_PARENT,WRAP_CONTENT);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    send_otp();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(View view){
        phone_no = view.findViewById(R.id.phone_no);
        submit = view.findViewById(R.id.submit);

        parent = (LoginActivity) getActivity();
    }

    private void send_otp() throws JSONException {
        if(phone_no.getText().length() > 25)
            parent.show_error_message(phone_no,getResources().getString(R.string.signup_enter_phone_maximum_length_exceed));
        else if(phone_no.getText().length() == 0)
            parent.show_error_message(phone_no,getResources().getString(R.string.signup_enter_phone_empty));
        else {
            JSONObject arr = new JSONObject();
            arr.put("noHp", phone_no.getText().toString());

            parent.url = parent.BASE_URL + "/mobile/auth/forgot-password-sendotp";
            parent.show_wait_modal();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, arr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject response_data = response.getJSONObject("response");
                        if (!response_data.getString("type").equals("Failed")) {
                            parent.show_error_message(parent.layout, getResources().getString(R.string.otp_notif_resend));
                            parent.dismiss_wait_modal();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject data = new JSONObject();
                                        data.put("phone", phone_no.getText().toString());

                                        dismiss();
                                        startActivity(new Intent(parent, OtpActivity.class)
                                                .putExtra("data", data.toString()));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, parent.delay_time_for_alert);
                        } else {
                            parent.show_error_message(parent.layout, response_data.getJSONObject("message").getJSONArray("errorInfo").getString(2));
                            dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        parent.error_handling(error, parent.layout);
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
                    headers.put("Authorization", parent.user_SP.getString("access_token", ""));
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            parent.consume_api(jsonObjectRequest);
        }
    }
}
