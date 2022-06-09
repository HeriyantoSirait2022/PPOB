package com.qdi.rajapay.main_menu.insurance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.main_menu.electrical.ElectricalPriceListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.16 BPJS
 * @screen 4.16.1
 */
public class InsuranceInputNoActivity extends BaseActivity {
    ImageView image;
    EditText no;
    Button next;
    JSONObject fee_data;

    /**
     * @author Jesslyn
     * @note add flag for active and problem product
     */
    // <code>
    boolean isProblem = false, isActive = true;
    // </code>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_insurance_input_no);

        init_toolbar(getResources().getString(R.string.home_insurance));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // @CFI
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNext();
            }
        });
    }

    /**
     * @author Jesslyn
     * @note set function onNext
     */
    // <code>
    private void onNext(){
        if(no.getText().toString().equals(""))
            show_error_message(layout,getResources().getString(R.string.main_menu_insurance_empty_no));
        else if(isActiveNotProblem(isActive, isProblem)){
            /**
             * @author : Jesslyn
             * @note : change fee_data variable (get fee_data if not exist).
             */
            // <code>
            startActivity(new Intent(InsuranceInputNoActivity.this, InsurancePayToActivity.class)
                    .putExtra("no",no.getText().toString())
                    .putExtra("fee_data", fee_data.toString() ));
            // </code>
        }
    }
    // </code>

    private void init() throws JSONException {
        image = findViewById(R.id.image);
        no = findViewById(R.id.no);

        // @CFI
        next = findViewById(R.id.next);

        // <code>
        get_fee_data();
        preparePriceTag(this, ElectricalPriceListActivity.class);
        add_ons_view_array.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * @author Dinda
                 * @note case CICD 9453 - tag NPE
                 */
                // <code-9453>
                if (isActiveNotProblem(isActive, isProblem) && fee_data != null)
                    startActivity(new Intent(InsuranceInputNoActivity.this, InsurancePriceListActivity.class)
                            .putExtra("fee_data", fee_data.toString()));
                // </code-9453>
            }
        });
        // </code>

        /**
         * @author Jesslyn
         * @note init custom keyboard
         */
        // <code>
        initNumpadKeyboard(no, new NumpadKeyboardSubmit() {
            @Override
            public void onSubmit() {
                onNext();
            }
        });
        // </code>
    }


    /**
     * @author Jessln
     * @note set mode enabled for activity
     * @param enabled
     */
    // <code>
    private void setEnabled(boolean enabled){
        no.setFocusable(enabled);
        no.setEnabled(enabled);
        next.setEnabled(enabled);
        pk.setEnabled(enabled);
        if(enabled)
            pk.setVisibility(View.VISIBLE);
        else
            pk.setVisibility(View.GONE);
    }
    // </code>

    /**
     * @author : Jesslyn
     * @note : Add wait modal
     * @throws JSONException
     */
    private void get_fee_data() throws JSONException {
        show_wait_modal();
        url = BASE_URL+"/mobile/postpaid/bpjs-feeinfo";

        JSONObject param = getBaseAuth();
        param.put("productType", "BPJSKES");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        fee_data = response_data;
                        fee_data.put("text","Tagihan BPJS");
                        fee_data.put("price",response_data.getDouble("adminFee"));
                        fee_data.put("price_str","Rp. "+formatter.format(response_data.getDouble("adminFee")));
                        /**
                         * @author : Jesslyn
                         * @note : 1. if isActive == 1 then check isProblem, otherwise make ui disable
                         *         2. get isProblem flag and detail, maintain (disable) ui if isProblem == 1
                         */
                        // <code>
                        isActive = true;
                        if(fee_data.getInt("isProblem") == 0) {
                            isProblem = false;
                            fee_data.put("detail", !fee_data.isNull("detailToken") ? fee_data.getString("detailToken") : "-");
                            setEnabled(true);
                        }
                        else if(fee_data.getInt("isProblem") == 1){
                            isProblem = true;
                            fee_data.put("detail", getResources().getString(R.string.trouble_label));
                            isActiveNotProblem(isActive, isProblem);
                            setEnabled(false);
                        }
                        fee_data.put("is_important", fee_data.getInt("isProblem") == 1);
                        // </code>
                    } else {
                        isActive = false;
                        setEnabled(false);
                        // no need to call isActiveNotProblem cause response from server is unsure
                        show_error_message(layout,response_data.getString("message"));
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
                    /**
                     * @author Jesslyn
                     * @note check respond, if respond is Data Not Found, then for this activity assume is active = 0. Server not returned any data if isActive = 0
                     */
                    // <code>
                    isActive = false;
                    setEnabled(false);

                    String response_data = new String(error.networkResponse.data, "UTF-8");
                    JSONObject data = new JSONObject(response_data);
                    if (data.has("response")) {
                        JSONObject data_response = data.getJSONObject("response");
                        if (data_response.has("message")) {
                            String message = data_response.getString("message");

                            if(message.equalsIgnoreCase(getStr(R.string.data_not_found))){
                                dismiss_wait_modal();
                                // assume is inactive problem (because if isActive = 0, WS not returned any data
                                isActiveNotProblem(isActive, isProblem);
                            }else{
                                error_handling(error, layout);
                            }
                        }
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
        consume_api(jsonObjectRequest);
    }
}