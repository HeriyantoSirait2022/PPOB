package com.qdi.rajapay.payment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.home.MainActivity;
import com.qdi.rajapay.model.enums.ResponseCode;
import com.qdi.rajapay.utils.NumberKeyboard;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.10 Prabayar Data
 * @screen 4.10.9++
 */
public class PaymentPinActivity extends BaseActivity {
    EditText pin;
    Button submit;
    NumberKeyboard nk;
    InputConnection ic;

    JSONObject data, coupon_data, user_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_pin);

        init_toolbar(getResources().getString(R.string.activity_title_payment_pin));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pin.getText().toString().equals(""))
                    show_error_message(layout,getResources().getString(R.string.payment_enter_pin_empty));
                else if(pin.getText().length() < 6)
                    show_error_message(layout,getResources().getString(R.string.account_manage_account_change_pin_length_not_satisfied));
                else {
                    try {
                        /**
                         * @author Dinda
                         * @note CICD Case 8733 - check coupon qty first before settle payment
                         */
                        if(coupon_data.toString().equals("{}"))
                            pay_main_menu();
                        else
                            send_coupon();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void init() throws JSONException {
        pin = findViewById(R.id.pin);
        submit = findViewById(R.id.submit);

        pin.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        pin.requestFocus();
        nk = findViewById(R.id.number_keyboard);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        ic = pin.onCreateInputConnection(new EditorInfo());
        nk.setInputConnection(ic);

        data = new JSONObject(getIntent().getStringExtra("data"));
        coupon_data = new JSONObject(getIntent().getStringExtra("coupon_data"));
        user_data = new JSONObject(user_SP.getString("user","{}"));
    }

    private void pay_main_menu() throws JSONException {
        final JSONObject arr = add_data_to_pay();

        url = BASE_URL + data.getString("url_pay");
        Log.d("url",url);
        Log.d("arr",arr.toString());
        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        data.put("invoice_request",arr);
                        /**
                         * @author Jesslyn
                         * @note Case TDD 12491 - handle manualAdvice response
                         *       9983 manual advice for PREPAIDPLNB (condition at SuccessActivity and at the function to display short message)
                         * @throws JSONException
                         */
                        // <code>
                        if(response_data.has("responseCode"))
                            redirect_with_response_code(response_data.getString("responseCode"));
                        else
                            redirect_to_success();
                        // </code>
                    } else {
                        show_error_message(layout, response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {/**
                 * @author Dinda
                 * @note check respond, if respond is ref id tidak ditemukan then intent to MainActivity
                 */
                    // <code>
                    String message = getErrorType(error);
                    if(message.isEmpty()) {
                        String response_data = new String(error.networkResponse.data, "UTF-8");
                        JSONObject data = new JSONObject(response_data);
                        if (data.has("response")) {
                            JSONObject data_response = data.getJSONObject("response");
                            if (data_response.has("message")) {
                                message = data_response.getString("message");

                                if (message.equalsIgnoreCase(getStr(R.string.refid_not_found))) {
                                    dismiss_wait_modal();
                                    redirect_to_failed();
                                } else {
                                    message = error_handling(error, layout);
                                    redirect_to_failed(message);
                                }
                            }
                        }
                    }else{
                        redirect_to_failed(message);
                    }
                    // </code>
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

        consume_api(jsonObjectRequest, paymentRetryPolicy);
    }

    private void send_coupon() throws JSONException {
        JSONObject invoice_data = data.getJSONObject("invoice_data");
        JSONObject data_post = data.getJSONObject("data_post");

        JSONObject arr = new JSONObject();
        arr.put("idOrder",invoice_data.getString("idOrder"));
        arr.put("idCoupon",coupon_data.getInt("idCoupon"));
        arr.put("cdeCoupon",coupon_data.getString("cdeCoupon"));
        arr.put("categoryCoupon",coupon_data.getString("categoryCoupon"));
        arr.put("typeCoupon",coupon_data.getString("typeCoupon"));
        arr.put("idProduct",data_post.getString("idProduct"));
        arr.put("expCoupon",coupon_data.getString("expCoupon"));
        arr.put("amount",coupon_data.getDouble("price") * -1);
        arr.put("idLogin",user_SP.getString("idLogin",""));
        arr.put("idUser",user_SP.getString("idUser",""));
        arr.put("token",user_SP.getString("token",""));
        /**
         * @author Jesslyb
         * @note add PIN to parameter to check pin (only works for this activity, different with ChoosePayementActivity.java)
         */
        // <code>
        arr.put("pin",pin.getText().toString());
        // </code>

        url = BASE_URL + "/mobile/coupon/send";

        Log.d("url",url);
        Log.d("arr",arr.toString());

        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed"))
                        pay_main_menu();
                    else {
                        show_error_message(layout, response_data.getString("message"));
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

    private JSONObject add_data_to_pay() throws JSONException {
        JSONObject invoice_data = data.getJSONObject("invoice_data");
        invoice_data.remove("type");
        invoice_data.remove("code");
        invoice_data.remove("message");
        invoice_data.remove("description");
        /**
         * @author Dinda
         * @note Let base activity manage usedRefCode parameter
         * <code>
         *     invoice_data.put("usedRefCde",user_data.getString("usedRefCde"));
         * </code>
         */
        // <code>
        setUserRefCde(invoice_data);
        // </code>
        invoice_data.put("pin",pin.getText().toString());
        invoice_data.put("paymentMtd","DEPOSIT");
        invoice_data.put("totBillAmount", invoice_data.getString("productCategory").equals("PREPAID") ?
                invoice_data.getDouble("billAmount") :
                invoice_data.getDouble("totBillAmount"));
        if(!invoice_data.has("totBillAmountVendor"))
            invoice_data.put("totBillAmountVendor", invoice_data.getDouble("totBillAmount"));
        /**
         * @author Dinda
         * @note case tdd 12445 - add new data
         *       1. add MSN
         *       2. add $curl_response->data->subscriberID
         *       3. add vendorAdmin
         */
        // <code>
        if(invoice_data.has("vendorAdmin"))
            invoice_data.put("vendorAdmin", invoice_data.getDouble("vendorAdmin"));
        // </code>

        if(!coupon_data.toString().equals("{}")){
            invoice_data.put("cdeCoupon",coupon_data.getString("cdeCoupon"));
            Double discount = coupon_data.getDouble("price");
            if(discount < 0)
                discount *= -1;
            invoice_data.put("couponAmount",discount);
            invoice_data.put("totBillAmount",invoice_data.getDouble("totBillAmount") - discount);
        }

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("idOrder",invoice_data.getString("idOrder"));
//        jsonObject.put("dtOrder",invoice_data.getString("dtOrder"));
//        jsonObject.put("cdeProduct",invoice_data.getString("cdeProduct"));
//        jsonObject.put("paymentMtd","DEPOSIT");
//        jsonObject.put("typeTxn",invoice_data.getString("typeTxn"));
//        if(invoice_data.getString("productCategory").equals("PREPAID")){
//            jsonObject.put("billAmount",invoice_data.getString("billAmount"));
//            jsonObject.put("vendorAmount",invoice_data.getString("vendorAmount"));
//            jsonObject.put("totBillAmountVendor",invoice_data.getString("billAmount"));
//            jsonObject.put("totBillAmount",invoice_data.getString("billAmount"));
//            jsonObject.put("noHp",invoice_data.has("noHp") ? invoice_data.getString("noHp") : "");
//            jsonObject.put("codePulsa",invoice_data.has("codePulsa") ? invoice_data.getString("codePulsa") : "");
//        }
//        else {
//            jsonObject.put("totBillAmount", invoice_data.getString("totBillAmount"));
//            jsonObject.put("totBillAmountVendor",invoice_data.getString("totBillAmountVendor"));
//        }
//        if(!coupon_data.toString().equals("{}")){
//            jsonObject.put("cdeCoupon",coupon_data.getString("cdeCoupon"));
//            jsonObject.put("couponAmount",coupon_data.getString("couponAmount"));
//        }
////        jsonObject.put("totBillAmountVendor",invoice_data.getString("totBillAmountVendor"));
//        jsonObject.put("pin",pin.getText().toString());
//        jsonObject.put("idRef",!invoice_data.isNull("idRef") ? invoice_data.getString("idRef") : "");
//        jsonObject.put("idLogin",user_SP.getString("idLogin",""));
//        jsonObject.put("idUser",user_SP.getString("idUser",""));
//        jsonObject.put("token",user_SP.getString("token",""));

        return invoice_data;
    }

    private void redirect_to_success() throws JSONException {
        show_error_message(layout, "Pembayaran anda sudah diterima!");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(PaymentPinActivity.this, SuccessActivity.class)
                        .putExtra("data", data.toString()));
            }
        }, delay_time_for_alert);
    }

    /**
     * @author Jesslyn
     * @note Case TDD 12491 - handle manualAdvice response
     * @throws JSONException
     */
    // <code>
    private void redirect_with_response_code(final String responseCode) throws JSONException {
        if(responseCode.equalsIgnoreCase(ResponseCode.MANUAL_ADVICE.toString()))
            show_error_message(layout, "Transaksi perlu tindakan!");
        /**
         * @author Jesslyn
         * @patch FR19022
         * @notes add notification for otomax pending message
         */
        // <code>
        else if(responseCode.equalsIgnoreCase(ResponseCode.OTOMAX_PENDING.toString()))
            show_error_message(layout, "Transaksi sedang di proses!");
        // </code>
        else
            show_error_message(layout, "Pembayaran anda sudah diterima!");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(PaymentPinActivity.this, SuccessActivity.class)
                        .putExtra("responseCode", responseCode)
                        .putExtra("data", data.toString()));
            }
        }, delay_time_for_alert);
    }
    // </code>

    private void redirect_to_failed() throws JSONException{
        user_edit_SP.putString("bottom_main_menu","home");
        user_edit_SP.commit();

        Intent intent = new Intent(PaymentPinActivity.this, MainActivity.class);
        JSONObject data = new JSONObject();
        data.put("dMsg", getStr(R.string.f_transaction_failed));
        intent.putExtra("data", data.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        finish();
        startActivity(intent);
    }

    private void redirect_to_failed(String message) throws JSONException{
        user_edit_SP.putString("bottom_main_menu","home");
        user_edit_SP.commit();

        Intent intent = new Intent(PaymentPinActivity.this, MainActivity.class);
        JSONObject data = new JSONObject();
        data.put("dMsg", message);
        intent.putExtra("data", data.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        finish();
        startActivity(intent);
    }
}