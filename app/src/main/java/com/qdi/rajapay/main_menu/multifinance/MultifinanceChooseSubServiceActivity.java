package com.qdi.rajapay.main_menu.multifinance;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MultifinanceChooseSubServiceActivity extends BaseActivity {
    RecyclerView list;

    MultifinanceChooseSubServiceAdapter adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<JSONObject> array = new ArrayList<>();

    JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_multifinance_choose_sub_service);

        init_toolbar(getResources().getString(R.string.home_multifinance));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setOnItemClickListener(new MultifinanceChooseSubServiceAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(MultifinanceChooseSubServiceActivity.this, MultifinanceInputNoActivity.class)
                        .putExtra("data_sub_service",array.get(position).toString())
                        .putExtra("data_provider",data.toString()));
            }
        });
    }

    private void init() throws JSONException {
        list = findViewById(R.id.list);

        prepare_data();
        adapter = new MultifinanceChooseSubServiceAdapter(array,this);
        layout_manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
        list.setLayoutManager(layout_manager);
    }

    private void prepare_data() throws JSONException {
        data = new JSONObject(getIntent().getStringExtra("data_provider"));

        for(int x=0;x<data.getJSONArray("arr").length();x++)
            array.add(data.getJSONArray("arr").getJSONObject(x));
    }
}