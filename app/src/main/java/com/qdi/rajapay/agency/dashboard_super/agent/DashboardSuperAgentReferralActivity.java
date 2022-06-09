package com.qdi.rajapay.agency.dashboard_super.agent;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;
import com.qdi.rajapay.agency.dashboard_super.commition.DashboardSuperCommitionDetailAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class DashboardSuperAgentReferralActivity extends BaseActivity {
    TextView price;
    RecyclerView list;
    View no_data_layout;

    JSONObject filter_data = new JSONObject();
    JSONObject data = new JSONObject();

    DashboardSuperCommitionDetailAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    Double total_data = 10000d;
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_super_bonus_referral);

        init_toolbar(getResources().getString(R.string.agency_dashboard_agent_referral));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);
        price = findViewById(R.id.price);
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
        price.setText(formatter.format(total_data)+" "+getResources().getString(R.string.agency_dashboard_agent));
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
            jsonObject.put("price_str", data.getJSONArray("arr").getJSONObject(x).getString("registerDate"));
            array.add(jsonObject);
        }

//        jsonObject = new JSONObject();
//        jsonObject.put("text", "Toko Sukses");
//        jsonObject.put("price", 50);
//        jsonObject.put("price_str", formatter.format(50)+" "+getResources().getString(R.string.agency_dashboard_agent));
//        array.add(jsonObject);
    }
}