package com.qdi.rajapay.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.R;
import com.qdi.rajapay.auth.login.LoginActivity;
import com.qdi.rajapay.home.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class AccountLogoutModal extends DialogFragment {
    Button yes,no;

    MainActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_logout_modal,container,false);
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

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    logout();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void init(View view){
        yes = view.findViewById(R.id.yes);
        no = view.findViewById(R.id.no);

        parent = (MainActivity) getActivity();
    }

    private void logout() throws JSONException {
        JSONObject arr = new JSONObject();
        arr.put("idLogin",parent.user_SP.getString("idLogin",""));
        arr.put("token",parent.user_SP.getString("token",""));
        arr.put("idUser",parent.user_SP.getString("idUser",""));

        parent.url = parent.BASE_URL+"/mobile/auth/logout";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, parent.url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        parent.user_edit_SP.remove("token");
                        parent.user_edit_SP.remove("idLogin");
                        parent.user_edit_SP.remove("idUser");
                        parent.user_edit_SP.remove("bottom_main_menu");
                        parent.user_edit_SP.remove("balance_deposit");
                        parent.user_edit_SP.remove("user");
                        parent.user_edit_SP.remove("pin_verified_status");
                        /**
                         * @author Jesslyn
                         * @note 0721531311-179 E05 New user required to change PIN at the first time. New feature would be enhanced at 4.10 - 4.20
                         */
                        // <code>
                        parent.user_edit_SP.remove("fcm_token_status");
                        parent.user_edit_SP.remove("fcm_token");
                        // </code>
                        parent.user_edit_SP.commit();

                        dismiss();
                        startActivity(new Intent(parent, LoginActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        parent.show_error_message(parent.layout,response_data.getString("message"));
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
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        parent.consume_api(jsonObjectRequest);
    }
}
