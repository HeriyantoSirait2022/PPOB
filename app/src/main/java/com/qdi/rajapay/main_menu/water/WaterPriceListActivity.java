package com.qdi.rajapay.main_menu.water;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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
 * @module 4.17 PDAM
 * @screen 4.17.7
 */
public class WaterPriceListActivity extends BaseActivity {
    RecyclerView list;

    WaterPriceListAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    JSONArray data_provider = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_water_price_list);

        init_toolbar(getResources().getString(R.string.activity_title_price_list));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new WaterPriceListAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                /**
                 * @author : Jesslyn
                 * @note : disable click action
                 */
                // <code>
                /*
                startActivity(new Intent(WaterPriceListActivity.this, WaterInputNoActivity.class)
                        .putExtra("data_provider",getIntent().getStringExtra("data_provider")));
                 */
                // </code>
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

        adapter = new WaterPriceListAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    /**
     * @author : Jesslyn
     * @note : add backup method if intent doesn't exist (following documentation)
     */
    // <code>
    private void get_data() throws JSONException {
        show_wait_modal();
        url = BASE_URL+"/mobile/postpaid/pdam-feeinfo";

        JSONObject param = getBaseAuth();
        param.put("productType", "PDAM");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        JSONArray jsonArray = response_data.getJSONArray("arrFeePdamRslt");
                        array.clear();
                        for(int x = 0; x < jsonArray.length(); x++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(x);
                            jsonObject.put("text",jsonObject.getString("nameProduct"));
                            jsonObject.put("price",jsonObject.getDouble("adminFee"));
                            jsonObject.put("price_str","Rp. "+formatter.format(jsonObject.getDouble("adminFee")));
                            jsonObject.put("is_trouble",jsonObject.getInt("isProblem") == 0 ? false : true);

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

    private void prepare_data() throws JSONException {
        for(int x = 0; x < data_provider.length(); x++)
            array.add(data_provider.getJSONObject(x));
    }
}