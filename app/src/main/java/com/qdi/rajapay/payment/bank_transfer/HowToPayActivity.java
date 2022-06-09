package com.qdi.rajapay.payment.bank_transfer;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.qdi.rajapay.BaseActivity;
import com.qdi.rajapay.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @module 4.1.4 Bank Transfer
 * @screen 4.1.4.3.A
 * @screen 4.1.4.3.B
 */
public class HowToPayActivity extends BaseActivity {
    TextView bank_name;
    ExpandableListView list;

    JSONObject data;

    HowToPayAdapter adapter;
    ArrayList<JSONObject> array_header = new ArrayList<>();
    HashMap<JSONObject,ArrayList<JSONObject>> array_child = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer_how_to_pay);

        init_toolbar( getResources().getString(R.string.activity_title_how_to_pay));
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init() throws JSONException {
        bank_name = findViewById(R.id.bank_name);
        list = findViewById(R.id.list);

        data = new JSONObject(getIntent().getStringExtra("payment"));
        bank_name.setText(data.getString("name"));

        adapter = new HowToPayAdapter(this,array_header,array_child);
        list.setAdapter(adapter);
    }
}