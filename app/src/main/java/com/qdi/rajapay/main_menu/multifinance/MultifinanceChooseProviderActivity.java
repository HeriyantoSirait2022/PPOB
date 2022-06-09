package com.qdi.rajapay.main_menu.multifinance;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.main_menu.postpaid_data.PostpaidDataPriceListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.20 Multifinance
 * @screen 4.20.1
 */
public class MultifinanceChooseProviderActivity extends BaseActivity {
    RecyclerView list;

    MultifinanceChooseProviderAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>(), add_ons_array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_multifinance_choose_provider);

        init_toolbar(getResources().getString(R.string.home_multifinance));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new MultifinanceChooseProviderAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                try {
                    if(array.get(position).getInt("isProblem") == 0)
                        /**
                         * @author : Jesslyn
                         * @note : change behavior for sending extras
                         */
                        // <code>
                        startActivity(new Intent(MultifinanceChooseProviderActivity.this, MultifinanceInputNoActivity.class)
                                .putExtra("data_provider",array.toString())
                                .putExtra("selected", position));
                        // </code>
                    else
                        show_error_message(layout,getResources().getString(R.string.trouble_click_label));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);

        adapter = new MultifinanceChooseProviderAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        get_data();

        /**
         * @author : Jesslyn
         * @note : 1. add Price Tag Icon
         *         2. over ride event onClick on Base Activity
         */
        // </code>
        preparePriceTag(this, PostpaidDataPriceListActivity.class);
        add_ons_view_array.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MultifinanceChooseProviderActivity.this, MultifinancePriceListActivity.class)
                        .putExtra("data_provider",array.toString()));
            }
        });
        // </code>
    }

    private void prepare_data() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("image",R.drawable.icon_building);
        add_ons_array.add(jsonObject);
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
                        array.clear();
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
                            array.add(jsonObject);
                        }
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
}