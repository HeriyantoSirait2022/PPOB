package com.qdi.rajapay.agency.target;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @module 7.0 Keagenan
 * @screen 7.2.1
 */
public class TargetIndexActivity extends BaseActivity {
    TextView target_this_month;
    RecyclerView list;

    TargetIndexAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_target_index);

        init_toolbar(getResources().getString(R.string.agency_target));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        target_this_month = findViewById(R.id.target_this_month);

//        prepare_data();
        adapter = new TargetIndexAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        get_data();
    }

    private void get_data() {
        url = BASE_URL+"/mobile/agen/target-transaction";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, getBaseAuth(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        JSONArray arr = response_data.getJSONArray("arrHistTarget");
                        for(int i = 0; i < arr.length(); i++){
                            SimpleDateFormat parse_date = new SimpleDateFormat("yyyy-MM");
                            SimpleDateFormat format_date = new SimpleDateFormat("MMMM yyyy");

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("time", format_date.format(parse_date.parse(arr.getJSONObject(i).getString("historyMonth"))));
                            jsonObject.put("detail", arr.getJSONObject(i).getString("totTxn")+"x Transaksi");
                            jsonObject.put("status", arr.getJSONObject(i).getString("status").equals("S") ?
                                    "Sukses" : "Gagal");
                            array.add(jsonObject);
                        }
                        adapter.notifyDataSetChanged();

                        target_this_month.setText(response_data.getString("targetThisMonth")+getResources().getString(R.string.agency_target_more));
                    } else {
                        show_error_message(layout,response_data.getString("message"));
                    }
                } catch (JSONException | ParseException e) {
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
}