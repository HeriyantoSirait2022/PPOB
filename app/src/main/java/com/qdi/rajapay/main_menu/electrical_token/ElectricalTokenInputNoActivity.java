package com.qdi.rajapay.main_menu.electrical_token;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.12 Prabayar Token PLN
 * @screen 4.12.1
 * @note CFI
 */
public class ElectricalTokenInputNoActivity extends BaseActivity {
    EditText no;
    Button next;

    // PhoneKeyboard pk;
    // InputConnection ic;

    ArrayList<JSONObject> array = new ArrayList<>();
    JSONObject data = new JSONObject();

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
        setContentView(R.layout.activity_main_menu_electrical_token_input_no);

        init_toolbar(getResources().getString(R.string.home_electrical_token));
        init();

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
        try {
            if(no.getText().toString().equals(""))
                show_error_message(layout,getResources().getString(R.string.main_menu_water_empty_no));
            else {
                get_data();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // </code>

    private void init() {
        no = findViewById(R.id.no);
        next = findViewById(R.id.next);

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

    private void get_data() throws JSONException {
        url = BASE_URL+"/mobile/prepaid/tokenpln-info";

        JSONObject param = getBaseAuth();
        param.put("productType", "PLNPREPAIDB");
        param.put("noCust", no.getText().toString());

        show_wait_modal();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    isActive = true;
                    isProblem = false;
                    if (!response_data.getString("type").equals("Failed")) {
                        if(response_data.has("is_active") && response_data.getInt("is_active") == 0 ){
                            isActive = false;
                        }else if(response_data.has("is_problem") && response_data.getInt("is_problem") == 1){
                            isProblem = true;
                        }

                        if(isActiveNotProblem(isActive, isProblem)){
                            JSONArray jsonArray = response_data.getJSONArray("arrTokenPriceRslt");
                            array.clear();
                            for(int x = 0; x < jsonArray.length(); x++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(x);
                                if(!jsonObject.isNull("vendorAmount") && !jsonObject.isNull("nameToken")) {
                                    jsonObject.put("name", jsonObject.getString("nameToken"));
                                    if(jsonObject.getInt("isProblem") == 0)
                                        jsonObject.put("detail", !jsonObject.isNull("detailToken") ? jsonObject.getString("detailToken") : "-");
                                    else
                                        jsonObject.put("detail", getResources().getString(R.string.trouble_label));
                                    jsonObject.put("is_important", jsonObject.getInt("isProblem") == 1);
                                    jsonObject.put("price", jsonObject.getDouble("vendorAmount"));
                                    jsonObject.put("price_str", "Rp. " + formatter.format(jsonObject.getDouble("vendorAmount")));

                                    array.add(jsonObject);
                                }
                            }
                            data = response_data;

                            JSONObject data1 = new JSONObject();
                            data1.put("no",no.getText().toString());
                            data1.put("info",data);
                            data1.put("arr_token",new JSONArray(array.toString()));
                            data1.put("service_name",getResources().getString(R.string.home_electrical_token));

                            startActivity(new Intent(ElectricalTokenInputNoActivity.this, ElectricalTokenPriceListActivity.class)
                                    .putExtra("data", data1.toString()));
                        }
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    dismiss_wait_modal();
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