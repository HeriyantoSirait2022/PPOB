package com.qdi.rajapay.main_menu.electrical;

import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 4.14 Listrik
 * @screen 4.14.6
 */
public class ElectricalPriceListActivity extends BaseActivity {
    RecyclerView list;

    ElectricalPriceListAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_electrical_price_list);

        init_toolbar(getResources().getString(R.string.activity_title_price_list));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new ElectricalPriceListAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                /**
                 * @author : Jesslyn
                 * @note : disable click action, confirm Mr. Yo
                 */
                // <code>
                /*
                try {
                    if(array.get(position).getInt("isProblem") == 0)
                        startActivity(new Intent(ElectricalPriceListActivity.this, ElectricalInputNoActivity.class)
                                .putExtra("type","electrical")
                                .putExtra("fee_data",array.get(position).toString()));
                    else
                        show_error_message(layout,getResources().getString(R.string.trouble_click_label));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */
                // </code>
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);

        /**
         * @author : Jesslyn
         * @note : Change behavior for handing intent extras. if there weren't any extras, then get data from server
         */
        // <code>
        String extras = (String) isExtras(getIntent(), "fee_data");
        if(isNullOrEmpty(extras)){
            get_data();
        }else{
            JSONObject object = new JSONObject(extras);
            array.add(object);
        }
        // </code>

        adapter = new ElectricalPriceListAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    /**
     * @author : Jesslyn
     * @note : add wait modal
     * @throws JSONException
     */
    // <code>
    private void get_data() throws JSONException {
        show_wait_modal();
        url = BASE_URL+"/mobile/postpaid/pln-feeinfo";

        JSONObject param = getBaseAuth();
        param.put("productType", "PLN");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss_wait_modal();
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        array.clear();
                        JSONObject jsonObject = response_data;
                        jsonObject.put("text","Tagihan PLN");
                        jsonObject.put("price",response_data.getDouble("adminFee"));
                        jsonObject.put("price_str","Rp. "+formatter.format(response_data.getDouble("adminFee")));
                        if(jsonObject.getInt("isProblem") == 0)
                            jsonObject.put("detail", !jsonObject.isNull("detailToken") ? jsonObject.getString("detailToken") : "-");
                        else
                            jsonObject.put("detail", getResources().getString(R.string.trouble_label));
                        jsonObject.put("is_important", jsonObject.getInt("isProblem") == 1);
                        array.add(jsonObject);

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