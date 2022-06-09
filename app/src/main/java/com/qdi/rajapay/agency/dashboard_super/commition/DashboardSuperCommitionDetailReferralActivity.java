package com.qdi.rajapay.agency.dashboard_super.commition;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.agency.commition_history.CommitionHistoryIndexActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DashboardSuperCommitionDetailReferralActivity extends BaseActivity {
    TextView price;
    RecyclerView list;
    Button filter;
    View no_data_layout;

    JSONObject filter_data = new JSONObject();
    JSONObject data = new JSONObject();

    DashboardSuperCommitionDetailAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    CommitionHistoryIndexActivity parent;
    Double total_data = 10000d;
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_super_commition_detail_referral);

        init_toolbar(getResources().getString(R.string.agency_dashboard_commition_referral));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardSuperCommitionDetailReferralFilterModal modal = new DashboardSuperCommitionDetailReferralFilterModal();
                modal.show(getSupportFragmentManager(),"modal");
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        price = findViewById(R.id.price);
        filter = findViewById(R.id.filter);
        no_data_layout = findViewById(R.id.no_data_layout);

        data = new JSONObject(getIntent().getStringExtra("data"));
        total_data = data.getDouble("total");

        prepare_data();
        adapter = new DashboardSuperCommitionDetailAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);

        manage_content();
    }

    private void manage_content(){
        price.setText("Rp. "+formatter.format(total_data));
        if(array.size() > 0) {
            no_data_layout.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
        else {
            no_data_layout.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }
    }

    private void prepare_data() throws JSONException {
        for(int x = 0; x < data.getJSONArray("arr").length(); x++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", data.getJSONArray("arr").getJSONObject(x).getString("name"));
            jsonObject.put("price", data.getJSONArray("arr").getJSONObject(x).getDouble("totTransaction"));
            jsonObject.put("price_str", "Rp. "+formatter.format(data.getJSONArray("arr").getJSONObject(x).getDouble("totTransaction")));
            array.add(jsonObject);
        }
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("text", "Berkat Cell");
//        jsonObject.put("price", 50);
//        jsonObject.put("price_str", "Rp. "+formatter.format(50));
//        array.add(jsonObject);
//
//        jsonObject = new JSONObject();
//        jsonObject.put("text", "Toko Sukses");
//        jsonObject.put("price", 50);
//        jsonObject.put("price_str", "Rp. "+formatter.format(50));
//        array.add(jsonObject);
    }

    public void get_data() throws JSONException {
        url = BASE_URL+"/mobile/agen/dashboardsuper";

        JSONObject param = getBaseAuth();
        if(filter_data.has("month") && !filter_data.getString("month").equals("0"))
            param.put("month", filter_data.getString("month"));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject response_data = response.getJSONObject("response");
                    if (!response_data.getString("type").equals("Failed")) {
                        array.clear();
                        for(int x = 0; x < response_data.getJSONArray("arrComReferral").length(); x++){
                            if(response_data.getJSONArray("arrComReferral").getJSONObject(x).getDouble("totTransaction") > 0) {
                                JSONObject jsonObject = response_data.getJSONArray("arrComReferral").getJSONObject(x);
                                jsonObject.put("text", response_data.getJSONArray("arrComReferral").getJSONObject(x).getString("name"));
                                jsonObject.put("price", response_data.getJSONArray("arrComReferral").getJSONObject(x).getDouble("totTransaction"));
                                jsonObject.put("price_str", "Rp. " + formatter.format(response_data.getJSONArray("arrComReferral").getJSONObject(x).getDouble("totTransaction")));
                                array.add(jsonObject);
                            }
                        }
                        total_data = response_data.getDouble("totComReferral");

                        adapter.notifyDataSetChanged();
                        manage_content();
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
}