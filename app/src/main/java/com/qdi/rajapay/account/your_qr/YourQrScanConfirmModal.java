package com.qdi.rajapay.account.your_qr;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class YourQrScanConfirmModal extends DialogFragment {
    Button submit;
    EditText phone_no;
    TextView info;

    YourQrScanActivity parent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.your_qr_scan_confirm_modal,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(MATCH_PARENT,WRAP_CONTENT);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);

        parent.onDismiss();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            init(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    send_deposit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init(View view) throws JSONException {
        phone_no = view.findViewById(R.id.phone_no);
        submit = view.findViewById(R.id.submit);
        info = view.findViewById(R.id.info);

        parent = (YourQrScanActivity) getActivity();

        info.setText(String.format(
                getResources().getString(R.string.account_your_qr_scan_confirm_subtitle_modal),
                "Rp. "+parent.formatter.format(parent.result_data.getDouble("amount")),
                parent.result_data.getString("desc")
        ));
    }

    private void send_deposit() throws JSONException {
        parent.show_wait_modal();
        submit.setEnabled(false);

        JSONObject arr = new JSONObject();
        arr.put("idCollector",parent.result_data.getString("idCollector"));
        arr.put("qrCode",parent.result_data.getString("qrCode"));
        arr.put("amount",parent.result_data.getDouble("amount"));
        arr.put("desc",parent.result_data.getString("desc"));
        arr.put("idLogin",parent.user_SP.getString("idLogin",""));
        arr.put("idUser",parent.user_SP.getString("idUser",""));
        arr.put("token",parent.user_SP.getString("token",""));

        parent.url = parent.BASE_URL+"/mobile/account/send-deposit-qr";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    parent.dismiss_wait_modal();

                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        parent.show_error_message(parent.layout,getResources().getString(R.string.account_your_qr_scan_confirm_info_modal));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                submit.setEnabled(true);
                                dismiss();
                                parent.finish();
                            }
                        },parent.delay_time_for_alert);
                    } else {
                        parent.show_error_message(parent.layout,response_data.getJSONObject("message").getJSONArray("errorInfo").getString(2));
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
                    submit.setEnabled(true);
                    parent.error_handling(error, parent.layout);
                    dismiss();
                    parent.rescan();
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
