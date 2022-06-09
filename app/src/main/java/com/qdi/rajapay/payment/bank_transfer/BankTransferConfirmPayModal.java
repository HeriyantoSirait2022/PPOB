package com.qdi.rajapay.payment.bank_transfer;

import android.os.Bundle;
import android.util.Log;
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
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.order.OrderDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class BankTransferConfirmPayModal extends DialogFragment {
    Button cancel, ok;

    BaseActivity parent;

    JSONObject order_data;
    boolean shouldNavigateToMainOnDismiss = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bank_transfer_confirmation_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    submit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void init(View view){
        cancel = view.findViewById(R.id.cancel);
        ok = view.findViewById(R.id.ok);

        parent = (BaseActivity) getActivity();
    }

    public void setOrder_data(JSONObject order_data) {
        this.order_data = order_data;
    }

    public void setShouldNavigateToMainOnDismiss(boolean shouldNavigateToMainOnDismiss) {
        this.shouldNavigateToMainOnDismiss = shouldNavigateToMainOnDismiss;
    }

    private void submit() throws JSONException {
        JSONObject arr = new JSONObject();
        arr.put("idOrder",order_data.getString("idOrder"));
        arr.put("totBillAmount",order_data.getDouble("totBillAmount"));
        arr.put("idLogin",parent.user_SP.getString("idLogin",""));
        arr.put("idUser",parent.user_SP.getString("idUser",""));
        arr.put("token",parent.user_SP.getString("token",""));

        parent.url = parent.BASE_URL+"/mobile/deposit/manualconfirm-topup-bt";
        Log.d("url",parent.url);
        Log.d("data",arr.toString());
        parent.show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, parent.url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    parent.dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        /**
                         * @author Dinda
                         * @note Case CICD 10251 - Bank Transfer confirmation bad info / payment failed
                         *
                         * @author Jesslyn
                         * @note 0911254321-214 D01 Add "detail pembayaran" screen at 5.1.2 when payment transaction status "Menunggu Pembayaran" and payment type "Deposit"
                         *       add new activity result
                         */
                        // <code>
                        if(shouldNavigateToMainOnDismiss && (parent instanceof BankTransferPayActivity)) {
                            ((BankTransferPayActivity)parent).show_success(response_data);
                        } else if(shouldNavigateToMainOnDismiss && (parent instanceof BankTransferPayContActivity)) {
                            ((BankTransferPayContActivity)parent).show_success(response_data);
                        } else if((parent instanceof OrderDetailActivity)) {
                            ((OrderDetailActivity)parent).show_success(response_data);
                        }
                        dismiss();
                        // </code>
                    } else {
                        /**
                         * @author Liao Mei
                         * @note possible manual confirm bt data already success cause callback from transfer (automatically)
                         *       if it happened then show message at main activity
                         *
                         * @author Jesslyn
                         * @note 0911254321-214 D01 Add "detail pembayaran" screen at 5.1.2 when payment transaction status "Menunggu Pembayaran" and payment type "Deposit"
                         *       add new activity
                         */
                        // <code>
                        String message = response_data.getString("message");
                        int code = response_data.getInt("code");

                        dismiss();
                        if(code == 200){
                            // Pembayaran telah diterima
                            if(shouldNavigateToMainOnDismiss && (parent instanceof BankTransferPayActivity)) {
                                ((BankTransferPayActivity)parent).show_already_success(message);
                            } else if (shouldNavigateToMainOnDismiss && (parent instanceof BankTransferPayContActivity)) {
                                ((BankTransferPayContActivity)parent).show_already_success(message);
                            }else if((parent instanceof OrderDetailActivity)) {
                                ((OrderDetailActivity)parent).show_already_success(message);
                            }
                        }else{
                            // Error
                            // parent.show_error_message(parent.layout,response_data.getString("message"));
                            parent.redirectToMain(parent, message);
                        }
                        // </code>
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
