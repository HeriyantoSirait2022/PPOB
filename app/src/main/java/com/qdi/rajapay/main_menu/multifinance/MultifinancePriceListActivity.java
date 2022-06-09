package com.qdi.rajapay.main_menu.multifinance;

import android.os.Bundle;
import android.widget.ExpandableListView;

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

public class MultifinancePriceListActivity extends BaseActivity {
    ExpandableListView list;

    MultifinancePriceListAdapter adapter;
    ArrayList<JSONObject> array_header = new ArrayList<>();
    HashMap<JSONObject,ArrayList<JSONObject>> array_child = new HashMap<>();

    // <code>
    JSONArray data_provider = new JSONArray();
    // </code>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_phone_price_list);

        init_toolbar(getResources().getString(R.string.activity_title_price_list));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new MultifinancePriceListAdapter.ClickListener() {
            @Override
            public void onClick(int group_position, int child_position) {
                /**
                 * @author : Jesslyn
                 * @note : remove price list item event. Confirm Mr. Yo
                 */
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);

        /**
         * @author : Jesslyn
         * @note : Change behavior for handing extras
         */
        // <code>
        String extras = (String) isExtras(getIntent(), "data_provider");
        if(isNullOrEmpty(extras)){
            get_data();
        }else{
            data_provider = new JSONArray(extras);
            prepare_data();
        }
        // </code>
        adapter = new MultifinancePriceListAdapter(this,array_header,array_child);
        list.setAdapter(adapter);
    }

    /**
     * @author : Jesslyn
     * @note : add wait / dismiss modal
     * @throws JSONException
     */
    // <code>
    private void get_data() throws JSONException {
        show_wait_modal();
        url = BASE_URL+"/mobile/postpaid/multifinance-feeinfo";

        JSONObject param = getBaseAuth();
        param.put("productType", "MULTIFIN");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        data_provider = new JSONArray();
                        JSONArray jsonArray = response_data.getJSONArray("arrFeeMultifinanceRslt");
                        for(int x = 0; x < jsonArray.length(); x++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                            jsonObject.put("text",jsonObject.getString("nameProduct"));
                            /**
                             * @author : Jesslyn
                             * @note : 1. add price, price str into variable
                             *         2. add arr for PostpaidDataPriceListActivity
                             */
                            // <code>
                            jsonObject.put("price",jsonObject.getDouble("adminFee"));
                            jsonObject.put("price_str","Rp. "+formatter.format(jsonObject.getDouble("adminFee")));
                            // </code>
                            jsonObject.put("image_url",jsonObject.getString("image"));
                            jsonObject.put("image",null);
                            if(jsonObject.getString("cdeProduct").equals("FNCOLUMD") ||
                                    jsonObject.getString("cdeProduct").equals("FNWOMD")) {
                                jsonObject.put("url_info", "/mobile/postpaid/multifinance-info-wom");
                                jsonObject.put("url_post", "/mobile/postpaid/multifinance-transaction-wom");
                                jsonObject.put("url_pay", "/mobile/postpaid/multifinance-payment-wom");
                            }
                            else if(jsonObject.getString("cdeProduct").equals("FNMAF") ||
                                    jsonObject.getString("cdeProduct").equals("FNMEGA")) {
                                jsonObject.put("url_info", "/mobile/postpaid/multifinance-info-maf");
                                jsonObject.put("url_post", "/mobile/postpaid/multifinance-transaction-maf");
                                jsonObject.put("url_pay", "/mobile/postpaid/multifinance-payment-maf");
                            }
                            if(jsonObject.getInt("isProblem") == 0)
                                jsonObject.put("detail", !jsonObject.isNull("detailToken") ? jsonObject.getString("detailToken") : "-");
                            else
                                jsonObject.put("detail", getResources().getString(R.string.trouble_label));
                            jsonObject.put("is_important", jsonObject.getInt("isProblem") == 1);
                            // <code>
                            ArrayList<JSONObject> arrayList = new ArrayList<>();
                            arrayList.add(jsonObject);
                            jsonObject.put("arr",new JSONArray(arrayList.toString()));
                            // </code>
                            data_provider.put(jsonObject);
                        }
                        prepare_data();
                        adapter.notifyDataSetChanged();
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
    // </code>

    private void prepare_data() throws JSONException {
        /**
         * @author : Jesslyn
         * @note : set up data
         */
        // <code>
        for(int x = 0; x < data_provider.length(); x++) {
            array_header.add(data_provider.getJSONObject(x));

            ArrayList<JSONObject> arrayList = new ArrayList<>();
            for(int y = 0; y < data_provider.getJSONObject(x).getJSONArray("arr").length(); y++) {
                JSONObject jsonObject = data_provider.getJSONObject(x).getJSONArray("arr").getJSONObject(y);
                jsonObject.put("text",jsonObject.getString("text"));
                arrayList.add(jsonObject);
            }
            array_child.put(array_header.get(x), arrayList);
        }
        // </code>
    }
}